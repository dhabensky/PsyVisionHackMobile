package com.idp.emocore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idp.emocore.Data.PhotoData;

import java.util.Random;


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

	private TextView mTextMessage;
	private SpeechAPI mSpeechApi;
	private VoiceRecorder mVoiceRecorder;

	private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

		@Override
		public void onVoiceStart() {
			if (mSpeechApi != null) {
				mSpeechApi.startRecognizing(mVoiceRecorder.getSampleRate());
			}
		}

		@Override
		public void onVoice(byte[] data, int size) {
			if (mSpeechApi != null) {
				mSpeechApi.recognize(data, size);
			}
		}

		@Override
		public void onVoiceEnd() {
			if (mSpeechApi != null) {
				mSpeechApi.finishRecognizing();
			}
		}

	};


	private final SpeechAPI.Listener mSpeechServiceListener =
			new SpeechAPI.Listener() {
				@Override
				public void onSpeechRecognized(final String text, final boolean isFinal) {
					if (isFinal) {
						mVoiceRecorder.dismiss();
					}
					if (mTextMessage != null && !TextUtils.isEmpty(text)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (isFinal) {
									mTextMessage.setVisibility(View.GONE);
									mTextMessage.setText(null);
								} else {
									mTextMessage.setVisibility(View.VISIBLE);
									mTextMessage.setText(text);
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
				ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
		ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[] {
						Manifest.permission.CAMERA,
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.INTERNET,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				}, PERMISSION_REQUEST_CODE);
			}
		}
		else {
			mPermissionsGranted = true;
		}

		mMainController = MainController.getInstance();

		mStatus = (TextView) findViewById(R.id.status_message);
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

		mTextMessage = (TextView) findViewById(R.id.help_message);
		mOverlay = (ViewOverlay) findViewById(R.id.overlay);
		mSpeechApi = new SpeechAPI(MainActivity.this);
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
		mSpeechApi.removeListener(mSpeechServiceListener);
		if (mPermissionsGranted)
			releaseView();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSpeechApi.destroy();
		mSpeechApi = null;
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
//						mStatus.setVisibility(View.VISIBLE);
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
		DataGrabber.setAudioGrabber(this);

		final Handler handler = new Handler();

		final Analysis an = new Analysis(this);
		Runnable runnableCode = new Runnable() {
			@Override
			public void run() {
				an.check();
				handler.postDelayed(this, 2000);
			}
		};
		handler.postDelayed(runnableCode, 5000);



		startVoiceRecorder();
		mSpeechApi.addListener(mSpeechServiceListener);
	}

	private void releaseView() {
		DataGrabber.stopPhotoGrabber();
		mCamera.release();
		mCamera = null;
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeView(mPreview);
		mMainController.setCamera(null);
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
