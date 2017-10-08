package com.idp.emocore;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.rtp.AudioStream;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.idp.emocore.Data.AudioData;
import com.idp.emocore.Data.PhotoData;
import com.projects.alshell.vokaturi.EmotionProbabilities;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiAsyncResult;
import com.projects.alshell.vokaturi.VokaturiException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class Analysis {

    private long previous;
    private long timelimit;

    private Context context;


    public Analysis(Context context) {
        this.context = context;





        try {
            Vokaturi.getInstance(context);
        } catch (VokaturiException e) {
            e.printStackTrace();
        }
    }

    public PhotoData getNewestPhoto() {

        long max = timelimit;
        PhotoData result = null;
        for (PhotoData p : DataGrabber.getPhotos()) {
            if (p.getTimestamp() >= max && p.isReady()) {
                max = p.getTimestamp();
                result = p;
            }
        }
        return result;
    }

    public AudioData getNewestAudio() {

        long max = timelimit;
        AudioData result = null;
        for (AudioData p : DataGrabber.getAudioChunks()) {
            if (p.getTimestamp() >= max) {
                max = p.getTimestamp();
                result = p;
            }
        }
        return result;
    }

    public void check() {
        calculateTimeLimit();
        //clearOldData();
        //analyseAudio();
        PhotoData p = getNewestPhoto();
        AudioData a = getNewestAudio();

        if (p != null) Log.d("PHOTO RESULT", p.result.happines + "");
        if (a != null) Log.d("AUDIO RESULT", a.getData().Happiness + "");

        previous = System.currentTimeMillis();
    }

    private void calculateTimeLimit() {
        timelimit = System.currentTimeMillis() - 16000;
    }

    private void clearOldData() {
        calculateTimeLimit();
        for (Iterator<PhotoData> iterator = DataGrabber.getPhotos().iterator(); iterator.hasNext(); ) {
            PhotoData p = iterator.next();
            if (p.getTimestamp() < timelimit) {
                iterator.remove();
            }
        }

        for (Iterator<AudioData> iterator = DataGrabber.getAudioChunks().iterator(); iterator.hasNext(); ) {
            AudioData p = iterator.next();
            if (p.getTimestamp() < timelimit) {
                iterator.remove();
            }
        }
    }


    private void analyseAudio() {
    }
}
