package com.idp.emocore;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;

import com.idp.emocore.Data.PhotoData;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class AudioController {

    Context cont;

    public AudioController(Context cont) {
        this.cont = cont;
    }

    public void execute(){
        try {
            Vokaturi.getInstance(cont).startListeningForSpeech();
        } catch (VokaturiException e) {
            e.printStackTrace();
        }


        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                try {
                    DataGrabber.pushAudio(Vokaturi.getInstance(cont).stopListeningAndAnalyze());
                } catch (VokaturiException e) {
                    e.printStackTrace();
                }
                new AudioController(cont).execute();
            }
        };
        handler.postDelayed(runnableCode, 8000);
    }

}
