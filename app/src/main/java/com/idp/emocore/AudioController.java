package com.idp.emocore;

import android.content.Context;
import android.os.Handler;

import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class AudioController {

    Context analCunt;

    public AudioController(Context cont) {
        this.analCunt = cont;
    }

    public void execute(){
        try {
            Vokaturi.getInstance(analCunt).startListeningForSpeech();
        } catch (VokaturiException e) {
            e.printStackTrace();
        }


        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                try {
                    DataGrabber.pushAudio(Vokaturi.getInstance(analCunt).stopListeningAndAnalyze());
                } catch (VokaturiException e) {
                    e.printStackTrace();
                }
                new AudioController(analCunt).execute();
            }
        };
        handler.postDelayed(runnableCode, 8000);
    }

}
