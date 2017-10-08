package com.idp.emocore;

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
                photos.add(new PhotoData());
                System.out.println("new photo");
                handler.postDelayed(this, 2000);
            }
        };
// Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }

}
