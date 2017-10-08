package com.idp.emocore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

	private static final int PERMISSION_REQUEST_CODE = 666;

	private Camera mCamera;
	private CameraPreview mPreview;
	private MainController mMainController;
	private boolean mPermissionsGranted;
	private boolean mViewInitRequested;
	private TextView mStatus;
	private ViewOverlay mOverlay;
	private int mStatusNumber = -1;

	@BindView(R.id.textMessage)
	TextView textMessage;
	private List<String> stringList;
	private SpeechAPI speechAPI;
	private VoiceRecorder mVoiceRecorder;
//	private ArrayAdapter adapter;

	private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

		@Override
		public void onVoiceStart() {
			if (speechAPI != null) {
				speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
			}
		}

		@Override
		public void onVoice(byte[] data, int size) {
			if (speechAPI != null) {
				speechAPI.recognize(data, size);
			}
		}

		@Override
		public void onVoiceEnd() {
			if (speechAPI != null) {
				speechAPI.finishRecognizing();
			}
		}

	};

	private static final int RECORD_REQUEST_CODE = 101;

	private final SpeechAPI.Listener mSpeechServiceListener =
			new SpeechAPI.Listener() {
				@Override
				public void onSpeechRecognized(final String text, final boolean isFinal) {
					if (isFinal) {
						mVoiceRecorder.dismiss();
					}
					if (textMessage != null && !TextUtils.isEmpty(text)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (isFinal) {
									textMessage.setText(null);
									stringList.add(0,text);
//									adapter.notifyDataSetChanged();
								} else {
									textMessage.setText(text);
								}
							}
						});
					}
				}
			};



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[] {
						Manifest.permission.CAMERA,
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.INTERNET
				}, PERMISSION_REQUEST_CODE);
			}
		}
		else {
			mPermissionsGranted = true;
		}

		mMainController = MainController.getInstance();

		mStatus = (TextView) findViewById(R.id.textView);
		mStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainController.takePicture(new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						Log.d("CAMERA", "take photo. data.length = " + (data == null ? "(null)" : data.length));
					}
				});
			}
		});

		mOverlay = (ViewOverlay) findViewById(R.id.overlay);

		ButterKnife.bind(this);
		speechAPI = new SpeechAPI(MainActivity.this);
		stringList = new ArrayList<>();
//		adapter = new ArrayAdapter(this,
//				android.R.layout.simple_list_item_1, stringList);
//		listView.setAdapter(adapter); prikrutit' k view
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mPermissionsGranted)
			initView();
		else
			mViewInitRequested = true;

	}

	@Override
	protected void onStop() {
		stopVoiceRecorder();

		// Stop Cloud Speech API
		speechAPI.removeListener(mSpeechServiceListener);
		speechAPI.destroy();
		speechAPI = null;
		if (mPermissionsGranted)
			releaseView();
		super.onStop();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(1); // attempt to get a Camera instance
			c.setDisplayOrientation(0);
			Camera.Parameters params = c.getParameters();
			params.setPictureSize(1280, 720);
			c.setParameters(params);
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CODE) {
			for (int i = 0; i < grantResults.length; i++) {
				if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "PERMISSION NOT GRANTED. RESTART THE APPLICATION", Toast.LENGTH_SHORT).show();
					return;
				}
				onPermissionGranted();
			}
		}
		else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void onPermissionGranted() {
		mPermissionsGranted = true;
		if (mViewInitRequested) {
			initView();
			mViewInitRequested = false;
		}
	}

	private void initView() {
		mCamera = getCameraInstance();
		mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			@Override
			public void onFaceDetection(Camera.Face[] faces, Camera camera) {
				Rect faceRect;
				if (faces != null && faces.length > 0)
					faceRect = faces[0].rect;
				else
					faceRect = null;
				mOverlay.setFaceRect(faceRect);
				if (faceRect != null) {
					String[] statuses = getResources().getStringArray(R.array.face_status);
					if (mStatusNumber == -1) {
						mStatusNumber = new Random().nextInt(statuses.length);
						mStatus.setText(statuses[mStatusNumber]);
						mStatus.setVisibility(View.VISIBLE);
					}
				}
				else {
					mStatus.setVisibility(View.GONE);
					mStatusNumber = -1;
				}
			}
		});
		mCamera.startFaceDetection();

		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		mMainController.setCamera(mCamera);
		DataGrabber.setPhotoGrabber();
		startVoiceRecorder();
		speechAPI.addListener(mSpeechServiceListener);
	}

	private void releaseView() {
		DataGrabber.stopPhotoGrabber();
		mCamera.release();
		mCamera = null;
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeView(mPreview);
		mMainController.setCamera(null);
	}

	private int isGrantedPermission(String permission) {
		return ContextCompat.checkSelfPermission(this, permission);
	}

	private void makeRequest(String permission) {
		ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
	}

	private void startVoiceRecorder() {
		if (mVoiceRecorder != null) {
			mVoiceRecorder.stop();
		}
		mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
		mVoiceRecorder.start();
	}

	private void stopVoiceRecorder() {
		if (mVoiceRecorder != null) {
			mVoiceRecorder.stop();
			mVoiceRecorder = null;
		}
	}


}
