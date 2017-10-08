package com.idp.emocore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.idp.emocore.Data.AudioData;
import com.idp.emocore.Data.PhotoData;
import com.projects.alshell.vokaturi.EmotionProbabilities;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class DataGrabber {

    static Queue<PhotoData> photos = new LinkedList<PhotoData>();
    static Queue<AudioData> audioChunks = new LinkedList<AudioData>();


    public static void setPhotoGrabber() {
        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                MainController.getInstance().takePicture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        photos.add(new PhotoData(data));
                    }
                });
                handler.postDelayed(this, 3500);
            }
        };
        handler.postDelayed(runnableCode, 2000);
    }


    public static void setAudioGrabber(Context cont) {
        new AudioController(cont).execute();
    }

    public static void pushAudio(EmotionProbabilities data) {
        audioChunks.add(new AudioData(data));
        Log.d("VOICE", data.toString());
    }

    public static Queue<PhotoData> getPhotos() {
        return photos;
    }

    public static Queue<AudioData> getAudioChunks() {
        return audioChunks;
    }
}
