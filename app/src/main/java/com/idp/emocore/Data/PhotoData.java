package com.idp.emocore.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.idp.emocore.App;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

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

public class PhotoData extends DataFrame {

    private byte[] data;
    FaceApiResult result;
    File file;

    private boolean ready = false;

    //private json

    public PhotoData(byte[] data) {
        timestamp = System.currentTimeMillis();
        this.data = data;

        File outputDir = App.getContext().getCacheDir(); // context being the Activity pointer
        try {
            file = File.createTempFile("temp" + timestamp, ".jpg", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestAnalysis();
    }

    private void requestAnalysis() {
        if (isReady()) return;

        MediaType type
                = MediaType.parse("application/octet-stream");

        final OkHttpClient client = new OkHttpClient();

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);



        ByteBuffer baos = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(baos);
        byte[] b = baos.array();
        //String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        RequestBody body = RequestBody.create(type, data);
        Request request = new Request.Builder()
                .url("https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=emotion")
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Ocp-Apim-Subscription-Key", "8146de19d6ed4702aefaabac2b2165ec")
                .post(body)
                .build();

        System.out.println("request sended");
        try {
            System.out.println("body length: " + request.body().contentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                //JsonReader reader = new JsonReader();
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ready = true;
            }
        });
    }

    public boolean isReady() {
        return ready;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        System.out.println(new String(hexChars));
        return new String(hexChars);

    }
}
