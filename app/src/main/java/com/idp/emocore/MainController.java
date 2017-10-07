package com.idp.emocore;

import android.hardware.Camera;

/**
 * Created by dhabensky on 07.10.2017.
 */

public class MainController {

	private static MainController sInstance;

	public static MainController getInstance() {
		if (sInstance == null)
			sInstance = new MainController();
		return sInstance;
	}

	private MainController() {

	}

	public void setCamera(Camera mCamera) {
		this.mCamera = mCamera;
	}

	private Camera mCamera;


	public void takePicture(Camera.PictureCallback raw) {
		mCamera.takePicture(null, null, raw);
	}
}
