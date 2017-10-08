package com.idp.emocore.Data;

import android.app.DownloadManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;
import retrofit2.Retrofit;

/**
 * Created by ozvairon on 08.10.2017.
 */

public class PhotoData extends DataFrame{

    private byte[] data;
    FaceApiResult result;

    private boolean ready = false;

    //private json

    public PhotoData() {

    }

    private void requestAnalysis() {
        if (isReady()) return;
        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("ByteString"), ByteString.of(ByteBuffer.wrap(data)));
        Request request = new Request.Builder()
                .url("https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes={emotions}")
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Ocp-Apim-Subscription-Key", "8146de19d6ed4702aefaabac2b2165ec")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException());
                }
                System.out.println(response.toString());
                ready = true;
            }
        });
    }

    public boolean isReady() {
        return ready;
    }
}
