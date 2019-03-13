package com.gerryrun.childeducation.piano;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.bean.QuestionLife;
import com.gerryrun.childeducation.piano.bean.QuestionLife.DataBean;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
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
        progressDialog.setMessage("初始化资源中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        rlAnswer = findViewById(R.id.rl_answer);
        imMuLeft = findViewById(R.id.im_mubu_left);
        imMuRight = findViewById(R.id.im_mubu_right);
        imRightAnswer = findViewById(R.id.im_right_answer);

        findViewById(R.id.im_guess_return).setOnClickListener(v -> finish());

        mQuestion1 = findViewById(R.id.im_questions_1);
        mQuestion1.setOnClickListener(v -> answer(1));
        mQuestion2 = findViewById(R.id.im_questions_2);
        mQuestion2.setOnClickListener(v -> answer(2));
        mQuestion3 = findViewById(R.id.im_questions_3);
        mQuestion3.setOnClickListener(v -> answer(3));

        imJudge1 = findViewById(R.id.im_judge_1);
        imJudge2 = findViewById(R.id.im_judge_2);
        imJudge3 = findViewById(R.id.im_judge_3);


//        for (int i = 0; i < data.size(); i++) {
        nextQuestion(data.get(currentIndex++));
        size = data.size();

//        }
   /*     if (guessType == 1) {
            imRightAnswer.setImageResource(R.drawable.music_woshitingyinwang_qingwa);
            mQuestion1.setImageResource(R.drawable.music_tingyinwang_qinfwa_1);
            mQuestion2.setImageResource(R.drawable.music_tingyinwang_mao);
            mQuestion3.setImageResource(R.drawable.music_tingyinwang_gou);
        }*/
    }

    private void nextQuestion(DataBean dataBean) {
        progressDialog.show();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
            }
        }
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
            mediaPlayer.setOnPreparedListener(mp1 -> {
                runOnUiThread(() -> {
                    mediaPlayer.start();
                    progressDialog.dismiss();
                });
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                runOnUiThread(() -> {
                    Toast.makeText(GuessDifferentiate.this, "音频资源加载失败 :errorId: " + what + "  extra: " + extra, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                });
                return false;
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        Picasso.get().load(dataBean.getRight_pic()).into(imRightAnswer);
    }

    private void answer(int answer) {
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
                    if (currentIndex > size) {
                        Toast.makeText(GuessDifferentiate.this, "没有下一题了哦！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Handler().postDelayed(() -> {
                        isChecked = false;
                        imJudge1.setVisibility(View.INVISIBLE);
                        imJudge2.setVisibility(View.INVISIBLE);
                        imJudge3.setVisibility(View.INVISIBLE);
                        nextQuestion(data.get(currentIndex++));
                    }, 500);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imMuRight.startAnimation(rightTranslateAnimation);
        }, 1200);
    }
}
