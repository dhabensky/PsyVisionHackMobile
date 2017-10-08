package com.idp.emocore;

import android.hardware.Camera;
import android.os.Handler;

import com.idp.emocore.Data.PhotoData;

import java.util.LinkedList;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class DataGrabber {

    private static final int DELAY = 3000;
    private static Handler sHandler;

    private static Runnable sRunnable =  new Runnable() {
        @Override
        public void run() {
            MainController.getInstance().takePicture(new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    photos.add(new PhotoData(data));
                }
            });
            sHandler.postDelayed(this, 3000);
        }
    };
    static LinkedList<PhotoData> photos = new LinkedList<PhotoData>();


    public static void setPhotoGrabber() {
        sHandler = new Handler();
        sHandler.postDelayed(sRunnable, DELAY);
    }

    public static void stopPhotoGrabber() {
        sHandler.removeCallbacks(sRunnable);
        sHandler = null;
    }

}
