package com.idp.emocore;

import com.idp.emocore.Data.FaceApiResult;
import com.idp.emocore.Data.PhotoData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.ByteString;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by ozvairon on 08.10.2017.
 */


public class Api {

    Api api = null;
    public Api getInstance() {
        if (api == null) api = new Api();
        return api;
    }
    private final OkHttpClient client = new OkHttpClient();

    public void requestFaceApi(PhotoData data) {

    }


//    public interface Server {
//        @POST("detect")
//        Call<FaceApiResult> request(@Header String key, @Header @Body byte[] data);
//    }
//
//    Retrofit retrofit;
//
//    private Api() {
//        retrofit = new Retrofit.Builder()
//                .baseUrl("https://westcentralus.api.cognitive.microsoft.com/face/v1.0/") // Адрес сервера
//                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
//                .build();
//    }
//
//
//
//
//    public void requestFaceApi(byte[] data) {
//        Server service = retrofit.create(Server.class);
//        Call<FaceApiResult> call;// = service.request(data);
//
//        call.enqueue(new Callback<FaceApiResult>() {
//            @Override
//            public void onResponse(Call<FaceApiResult> call, Response<FaceApiResult> response) {
//                if (response.isSuccessful()) {
//                    System.out.println("DONE");
//                    // запрос выполнился успешно, сервер вернул Status 200
//                } else {
//                    // сервер вернул ошибку
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FaceApiResult> call, Throwable t) {
//                // ошибка во время выполнения запроса
//            }
//        });
//    }

}

