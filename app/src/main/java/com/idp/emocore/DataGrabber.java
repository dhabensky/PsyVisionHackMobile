package com.idp.emocore;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

import com.idp.emocore.Data.AudioData;
import com.idp.emocore.Data.PhotoData;
import com.projects.alshell.vokaturi.EmotionProbabilities;

import java.util.LinkedList;
import java.util.Queue;

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
    static Queue<AudioData> audioChunks = new LinkedList<AudioData>();


    public static void setPhotoGrabber() {
        sHandler = new Handler();
        sHandler.postDelayed(sRunnable, DELAY);
    }

    public static void stopPhotoGrabber() {
	    sHandler.removeCallbacks(sRunnable);
	    sHandler = null;
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
