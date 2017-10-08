package com.idp.emocore;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private MainController mMainController;
	private Rect mFaceRect;
	private TextView mStatus;
	private ViewOverlay mOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	}

	@Override
	public void onStart() {
		super.onStart();

		mCamera = getCameraInstance();
		mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			@Override
			public void onFaceDetection(Camera.Face[] faces, Camera camera) {
				if (faces != null && faces.length > 0) {
					mFaceRect = faces[0].rect;
				}
				else {
					mFaceRect = null;
				}
				mOverlay.setFaceRect(mFaceRect);
				mStatus.setVisibility(mFaceRect == null ? View.GONE : View.VISIBLE);
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
		mCamera.release();
		mCamera = null;
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeView(mPreview);
		mMainController.setCamera(null);
		super.onStop();
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
}
