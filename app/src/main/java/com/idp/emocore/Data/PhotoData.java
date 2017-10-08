package com.idp.emocore.Data;


import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class PhotoData extends DataFrame{

    private byte[] data;
    public Emotion result;

    private boolean ready = false;

    //private json

    public PhotoData(byte[] data) {
        timestamp = System.currentTimeMillis();
        this.data = data;
        requestAnalysis();
    }

    private void requestAnalysis() {
        if (isReady()) return;

        MediaType type
                = MediaType.parse("application/octet-stream");

        final OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(type, data);
        Request request = new Request.Builder()
                .url("https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=emotion")
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Ocp-Apim-Subscription-Key", "8146de19d6ed4702aefaabac2b2165ec")
                .post(body)
                .build();

        System.out.println("request sended");


        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                //e.printStackTrace();
                System.out.println("failed fully");
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException());
                }
                Gson gson = new Gson();

                try {

                    String s = response.body().string();
                    Log.d("JSON RESULT", s);
                    ListFaces faces = gson.fromJson("{\"list\":" + s + "}", ListFaces.class);
                    result = faces.list.get(0).faceAttributes.emotion;

                    ready = true;
                } catch (Exception ex) {
                    Log.d("JSON RESULT", "ERROR");
                    ex.getStackTrace();
                }
            }
        });
    }

    public boolean isReady() {
        return ready;
    }

}
