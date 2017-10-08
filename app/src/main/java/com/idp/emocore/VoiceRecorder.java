package com.idp.emocore;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import com.idp.emocore.Data.AudioData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class VoiceRecorder {

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    List<AudioData> data;

    VoiceRecorder(List<AudioData> data) {
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        this.data = data;
        startRecording();
    }





    private void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        startRecordingThread();

    }

    private void startRecordingThread() {
        Thread recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        short sData[] = new short[BufferElements2Rec];

        byte[] chunk = new byte[0];
        System.out.println("begin: " + System.currentTimeMillis());
        Log.d("AUDIO", "begin: " + System.currentTimeMillis() + " " + 2 * RECORDER_SAMPLERATE * 16);

        while (chunk.length < 2 * RECORDER_SAMPLERATE) {
            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println(sData.length);
                byte bData[] = short2byte(sData);
                chunk = concat(chunk, bData);
        }
        Log.d("AUDIO", "end: " + System.currentTimeMillis());
        new AudioData(chunk);
        startRecordingThread();



    }

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private byte[] concat(byte[] a, byte b[]) {
        byte[] res = new byte[a.length+b.length];
        for (int i = 1; i < a.length; i++) {
            res[i] = a[i];
        }

        for (int i = 1; i < b.length; i++) {
            res[i + a.length] = b[i];
        }
        return res;
    }




}
