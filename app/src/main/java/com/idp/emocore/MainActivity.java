package com.idp.emocore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private MainController mMainController;
	private Rect mFaceRect;
	private TextView mStatus;
//	private ViewOverlay mOverlay;
	private int mStatusNumber = -1;
	private boolean mPermissionsGranted;
	private static final int PERMISSION_REQUEST_CODE = 666;
	private boolean mViewInitRequested;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMainController = MainController.getInstance();

		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[] {
						Manifest.permission.CAMERA,
						Manifest.permission.INTERNET
				}, PERMISSION_REQUEST_CODE);
			}
		}
		else {
			mPermissionsGranted = true;
		}

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

//		mOverlay = (ViewOverlay) findViewById(R.id.overlay);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mPermissionsGranted)
			initView();
		else
			mViewInitRequested = true;
	}

	private void initView() {
		mCamera = getCameraInstance();
		mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			@Override
			public void onFaceDetection(Camera.Face[] faces, Camera camera) {
				if (faces != null && faces.length > 0)
					mFaceRect = faces[0].rect;
				else
					mFaceRect = null;
//				mOverlay.setFaceRect(mFaceRect);
				if (mFaceRect != null) {
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

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		mMainController.setCamera(mCamera);
	}

	@Override
	protected void onStop() {
		if (mPermissionsGranted)
			releaseView();
		super.onStop();
	}

	private void releaseView() {
		mCamera.release();
		mCamera = null;
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeView(mPreview);
		mMainController.setCamera(null);
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(1); // attempt to get a Camera instance
			c.setDisplayOrientation(90);
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
}
