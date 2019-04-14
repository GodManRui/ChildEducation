package com.gerryrun.childeducation.piano;

import android.app.ProgressDialog;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.bean.Constont;
import com.gerryrun.childeducation.piano.bean.QuestionLife;
import com.gerryrun.childeducation.piano.bean.QuestionLife.DataBean;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gerryrun.childeducation.piano.util.NetUtil.getQuestion;

public class GuessDifferentiate extends BaseActivity {

    private int guessType;  //1生活类  2乐器类
    private QuestionLife guessQuestion;  //1生活类  2乐器类
    private ImageView mQuestion1;
    private ImageView mQuestion2;
    private ImageView mQuestion3;
    private ImageView imJudge1;
    private ImageView imJudge2;
    private ImageView imJudge3;
    private Handler handler = new Handler();
    private RelativeLayout rlAnswer;
    private ImageView imMuLeft;
    private ImageView imMuRight;
    private ImageView imRightAnswer;
    private boolean isChecked;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private int size;
    private int currentIndex = 0;
    private List<DataBean> data;
    private View rlShutdown;
    private ImageView imBackLight;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyPlayer();
        try {
            Picasso.get().shutdown();
        } catch (Throwable ignore) {
        }
    }

    private void onDestroyPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_guess_answer);
        guessType = getIntent().getIntExtra("guess_type", 1);
        guessQuestion = (QuestionLife) getIntent().getSerializableExtra("guess_question");
        if (guessQuestion == null) {
            Toast.makeText(this, "题目数据获取失败!", Toast.LENGTH_LONG).show();
            finish();
        }
        data = guessQuestion.getData();
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("初始化资源...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        rlAnswer = findViewById(R.id.rl_answer);
        imMuLeft = findViewById(R.id.im_mubu_left);
        imMuRight = findViewById(R.id.im_mubu_right);
        imRightAnswer = findViewById(R.id.im_right_answer);

        findViewById(R.id.im_guess_return).setOnClickListener(v -> finish());

        mQuestion1 = findViewById(R.id.im_questions_1);
        mQuestion1.setOnClickListener(v -> clickAnswer());
        mQuestion2 = findViewById(R.id.im_questions_2);
        mQuestion2.setOnClickListener(v -> clickAnswer());
        mQuestion3 = findViewById(R.id.im_questions_3);
        mQuestion3.setOnClickListener(v -> clickAnswer());

        imJudge1 = findViewById(R.id.im_judge_1);
        imJudge2 = findViewById(R.id.im_judge_2);
        imJudge3 = findViewById(R.id.im_judge_3);

        rlShutdown = findViewById(R.id.rl_this_shutdown);
        imBackLight = findViewById(R.id.im_back_light);
        findViewById(R.id.im_next_group).setOnClickListener(v -> {
            data.clear();
            currentIndex = 0;
            getData();
        });
        findViewById(R.id.im_benlun_return).setOnClickListener(v -> {
            onDestroyPlayer();
            finish();
        });

        nextQuestion(data.get(currentIndex++));
        size = data.size();
    }

    private void getData() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        try {
            getQuestion(guessType == 1 ? Constont.QUESTION_LIFE : Constont.QUESTION_MUSICAL, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        Toast.makeText(GuessDifferentiate.this, "服务器链接失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        runOnUiThread(() -> {
                            if (progressDialog != null)
                                progressDialog.dismiss();
                            Toast.makeText(GuessDifferentiate.this, "服务器响应失败: " + response.code(), Toast.LENGTH_LONG).show();
                            finish();
                        });
                        return;
                    }
                    Gson gson = new Gson();
                    String responseStr = response.body().string();
                    QuestionLife questionLife = gson.fromJson(responseStr, QuestionLife.class);
                    if (questionLife != null) {
                        data = questionLife.getData();
                        runOnUiThread(() -> {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            if (data == null || data.size() <= 0) {
                                Toast.makeText(GuessDifferentiate.this, "没有下一轮了!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                nextQuestion(data.get(currentIndex++));
                                size = data.size();
                            }
                        });
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(GuessDifferentiate.this, "Response parse Exception !!! ", Toast.LENGTH_LONG).show();
                finish();
            });
        }
    }

    private void nextQuestion(DataBean dataBean) {
        progressDialog.show();
        onDestroyPlayer();
        HashMap<String, String> choose = dataBean.getChoose();
        int index = 1;
        for (String key : choose.keySet()) {
            boolean isRight = false;
            if (dataBean.getRight().equals(key)) {
                isRight = true;
            }
            String questionUrl = choose.get(key);
            switch (index) {
                case 1:
                    if (isRight) imJudge1.setImageResource(R.drawable.music_duihao);
                    else imJudge1.setImageResource(R.drawable.music_cuohao);
                    Picasso.get().load(questionUrl).into(mQuestion1);
                    index++;
                    break;
                case 2:
                    if (isRight) imJudge2.setImageResource(R.drawable.music_duihao);
                    else imJudge2.setImageResource(R.drawable.music_cuohao);
                    Picasso.get().load(questionUrl).into(mQuestion2);
                    index++;
                    break;
                case 3:
                    if (isRight) imJudge3.setImageResource(R.drawable.music_duihao);
                    else imJudge3.setImageResource(R.drawable.music_cuohao);
                    Picasso.get().load(questionUrl).into(mQuestion3);
                    index++;
                    break;
            }
        }
        rlAnswer.setVisibility(View.VISIBLE);
//        mediaPlayer = MediaPlayer.create(this, Uri.parse(dataBean.getVoice()));

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(dataBean.getVoice());
            mediaPlayer.setOnPreparedListener(mp1 -> runOnUiThread(() -> {
                mediaPlayer.start();
                progressDialog.dismiss();
                if (rlShutdown.getVisibility() == View.VISIBLE) {
                    rlShutdown.setVisibility(View.GONE);
                    imBackLight.clearAnimation();
                }
            }));
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                runOnUiThread(() -> {
                    Toast.makeText(GuessDifferentiate.this, "音频资源加载失败 :errorId: " + what + "  extra: " + extra, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    finish();
                });
                return false;
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        Picasso.get().load(dataBean.getRight_pic()).into(imRightAnswer);
        isChecked = false;
    }

    private void clickAnswer() {
        if (isChecked) return;
        isChecked = true;
        imJudge1.setVisibility(View.VISIBLE);
        imJudge2.setVisibility(View.VISIBLE);
        imJudge3.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            rlAnswer.setVisibility(View.GONE);
            TranslateAnimation leftTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f);
            TranslateAnimation rightTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f);
            leftTranslateAnimation.setDuration(2000);
            leftTranslateAnimation.setFillAfter(false);
            rightTranslateAnimation.setDuration(2000);
            rightTranslateAnimation.setFillAfter(false);
            imMuLeft.startAnimation(leftTranslateAnimation);
            leftTranslateAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imJudge1.setVisibility(View.INVISIBLE);
                    imJudge2.setVisibility(View.INVISIBLE);
                    imJudge3.setVisibility(View.INVISIBLE);
                    if (currentIndex >= size) {
//                        Toast.makeText(GuessDifferentiate.this, "没有下一题了哦！", Toast.LENGTH_SHORT).show();
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                        }
                        rlShutdown.setVisibility(View.VISIBLE);
                        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        LinearInterpolator lin = new LinearInterpolator();
                        rotate.setInterpolator(lin);
                        rotate.setDuration(15000);//设置动画持续时间
                        rotate.setRepeatCount(-1);//设置重复次数
                        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                        imBackLight.setAnimation(rotate);
                        return;
                    }
                    new Handler().postDelayed(() -> {
                        nextQuestion(data.get(currentIndex++));
                    }, 1000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imMuRight.startAnimation(rightTranslateAnimation);
        }, 1200);
    }
}
