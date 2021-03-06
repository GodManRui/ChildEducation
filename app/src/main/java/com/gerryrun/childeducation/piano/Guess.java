package com.gerryrun.childeducation.piano;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.bean.Constont;
import com.gerryrun.childeducation.piano.bean.QuestionLife;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.gerryrun.childeducation.piano.util.NetUtil.getQuestion;

public class Guess extends BaseActivity {

    private int select;
    private boolean isLoading;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_guess_type_select);
        setTitle("竞猜");
        initView();
    }

    private QuestionLife questionLife;

    private void initData(int i) {
        select = i;
        if (select == 0) return;
        if (isLoading) {
            Toast.makeText(this, "题目数据获取中..", Toast.LENGTH_SHORT).show();
        }
        progressDialog.show();
        isLoading = true;

        try {
            getQuestion(select == 1 ? Constont.QUESTION_LIFE : Constont.QUESTION_MUSICAL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    isLoading = false;
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(Guess.this, "服务器链接失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    isLoading = false;
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> Toast.makeText(Guess.this,
                                "服务器响应失败: " + response.code(), Toast.LENGTH_LONG).show());
                        return;
                    }
                    Gson gson = new Gson();
                    String responseStr = response.body().string();
                    questionLife = gson.fromJson(responseStr, QuestionLife.class);
                    startNextActivity();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        findViewById(R.id.im_guess_go_home).setOnClickListener(v -> finish());
        findViewById(R.id.im_guess_type_life).setOnClickListener(v -> initData(1));
        findViewById(R.id.im_guess_type_musical).setOnClickListener(v -> initData(2));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("数据获取中,请稍后..");
        progressDialog.setCancelable(false);
    }

    private void startNextActivity() {
        runOnUiThread(() -> {
            progressDialog.dismiss();
            Intent intent = new Intent(this, GuessDifferentiate.class);
            intent.putExtra("guess_type", select);
            intent.putExtra("guess_question", questionLife);
            startActivity(intent);
        });
    }
}
