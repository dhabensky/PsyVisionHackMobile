package com.idp.emocore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Base64;
import android.widget.ImageView;

import com.idp.emocore.Data.AudioData;
import com.idp.emocore.Data.PhotoData;

import java.util.LinkedList;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class DataGrabber {

    static LinkedList<PhotoData> photos = new LinkedList<PhotoData>();
    static LinkedList<AudioData> audioChunks = new LinkedList<AudioData>();






    public static void setPhotoGrabber(final ImageView iv) {
        final Handler handler = new Handler();
// Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                MainController.getInstance().takePicture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        photos.add(new PhotoData(data));
                        Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        iv.setImageBitmap(bMap);

                    }
                });


                handler.postDelayed(this, 3000);
            }
        };
// Start the initial runnable task by posting through the handler
        handler.postDelayed(runnableCode, 3000);
    }


    public static void setAudioGrabber() {
        new VoiceRecorder(audioChunks);
    }

}
