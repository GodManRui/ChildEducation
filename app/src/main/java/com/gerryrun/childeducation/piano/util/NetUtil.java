package com.gerryrun.childeducation.piano.util;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.Guess;
import com.gerryrun.childeducation.piano.bean.Constont;
import com.gerryrun.childeducation.piano.bean.QuestionLife;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetUtil {

    public static void getQuestion(String url, okhttp3.Callback callback) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        //异步，需要设置一个回调接口
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }
}
