package com.idp.emocore.Data;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class AudioData extends DataFrame{

    byte[] data;

    public AudioData(byte[] data) {
        timestamp = System.currentTimeMillis();

        this.data = data;

        System.out.println("CHUNK " + timestamp);

    }

}
