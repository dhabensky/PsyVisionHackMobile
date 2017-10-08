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
import android.widget.Switch;

import com.idp.emocore.Data.AudioData;
import com.idp.emocore.Data.Emotion;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class Analysis {

    private long previous;
    private long timelimit;

    private Context context;

    //public List<> results = new ArrayList<>();

    public Result result;


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

        Emotion res = new Emotion();

        if (p != null) {
            if (a != null) {
                res.anger = (a.getData().Anger + p.result.anger) / 2;
                res.fear = (a.getData().Fear + p.result.fear) / 2;
                res.sadness = (a.getData().Sadness + p.result.sadness) / 2;
                res.happiness = (a.getData().Happiness + p.result.happiness) / 2;
                res.neutral = (a.getData().Neutrality + p.result.neutral) / 2;
            } else {
                res.anger = p.result.anger;
                res.fear = p.result.fear;
                res.sadness = p.result.sadness;
                res.happiness = p.result.happiness;
                res.neutral =  p.result.neutral;

            }
        } else {
            if (a != null) {
                res.anger = a.getData().Anger;
                res.fear = a.getData().Fear;
                res.sadness = a.getData().Sadness;
                res.happiness = a.getData().Happiness;
                res.neutral = a.getData().Neutrality;
            } else {
                previous = System.currentTimeMillis();
                result = null;
                return;
            }
        }


        HashMap<String, Double>  map = new HashMap<>();
        map.put("anger", res.anger);
        map.put("fear", res.fear);
        map.put("sadness", res.sadness);
        map.put("happiness", res.happiness);
        map.put("neutral", res.neutral);


        String label = "";
        double value = 0;

        for (String k : map.keySet()) {
            if (map.get(k) >= value) {
                value = map.get(k);
                label = k;
            }
        }


        switch (label) {
            case "neutral": {

                if (value > 0.5) {
                    result = new Result();
                    result.text = "Пока что беседа проходит спокойно";
                    result.bgcolor = "999999";
                    result.fgcolor = "333333";
                } else {
                    result = null;
                }
                break;

            }

            case "angry": {
                if (value > 0.2) {
                    result = new Result();
                    result.text = "Что-то пошло не так и собеседник разозлился";
                    result.bgcolor = "331111";
                    result.fgcolor = "e7e7e7";
                } else {
                    result = null;
                }
                break;
            }

            case "hapiness": {
                if (value > 0.2) {
                    result = new Result();
                    result.text = "Вы хороший собеседник! Ваш товарищ радуется!";
                    result.bgcolor = "999999";
                    result.fgcolor = "333333";
                } else {
                    result = null;
                }
                break;
            }

            case "sadness": {
                if (value > 0.2) {
                    result = new Result();
                    result.text = "Собеседнику грустно. Поддержите его!";
                    result.bgcolor = "e7e7e7";
                    result.fgcolor = "494949";
                } else {
                    result = null;
                }
                break;
            }

            case "fear": {
                if (value > 0.2) {
                    result = new Result();
                    result.text = "Вы чем-то напугали человека";
                    result.bgcolor = "e7e7e7";
                    result.fgcolor = "666666";
                } else {
                    result = null;
                }
                break;
            }
        }





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

    public Result getResult() {
        return result;
    }


    class Result {
        String text;
        String bgcolor = "999999";
        String fgcolor = "333333";
    }
}
