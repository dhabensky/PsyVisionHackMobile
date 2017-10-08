package com.idp.emocore.Data;

import com.projects.alshell.vokaturi.EmotionProbabilities;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class AudioData extends DataFrame{

    EmotionProbabilities data;

    public AudioData(EmotionProbabilities data) {
        timestamp = System.currentTimeMillis();

        this.data = data;

    }

    public EmotionProbabilities getData() {
        return data;
    }
}
