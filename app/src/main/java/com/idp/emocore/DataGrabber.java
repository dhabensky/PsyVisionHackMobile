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






    public static void setPhotoGrabber() {
        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                MainController.getInstance().takePicture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        photos.add(new PhotoData(data));
                        Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        iv.setImageBitmap(bMap);
                    }
                });


                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnableCode, 3000);
    }


    public static void setAudioGrabber() {
        new VoiceRecorder(audioChunks);
    }

    public static void pushAudio(byte[] data) {
        audioChunks.add(new AudioData());
    }

}
