package com.idp.emocore;

import android.hardware.Camera;
import android.os.Handler;

import com.idp.emocore.Data.PhotoData;

import java.util.LinkedList;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class DataGrabber {

    static LinkedList<PhotoData> photos = new LinkedList<PhotoData>();



    public static void setPhotoGrabber() {
        final Handler handler = new Handler();
// Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                MainController.getInstance().takePicture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        photos.add(new PhotoData(data));
                    }
                });


                handler.postDelayed(this, 3000);
            }
        };
// Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }

}
