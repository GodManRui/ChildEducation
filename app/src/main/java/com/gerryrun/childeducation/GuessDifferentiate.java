package com.gerryrun.childeducation;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GuessDifferentiate extends BaseActivity {

    private int guessType;  //1生活类  2乐器类
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

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_guess_answer);
        guessType = getIntent().getIntExtra("guess_type", 1);
        initView();
    }

    private void initView() {
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
        if (guessType == 1) {
            imRightAnswer.setImageResource(R.drawable.music_woshitingyinwang_qingwa);
            mQuestion1.setImageResource(R.drawable.music_tingyinwang_qinfwa_1);
            mQuestion2.setImageResource(R.drawable.music_tingyinwang_mao);
            mQuestion3.setImageResource(R.drawable.music_tingyinwang_gou);
        }
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
            leftTranslateAnimation.setFillAfter(true);
            rightTranslateAnimation.setDuration(2000);
            rightTranslateAnimation.setFillAfter(true);
            imMuLeft.startAnimation(leftTranslateAnimation);
            imMuRight.startAnimation(rightTranslateAnimation);
        }, 1500);
    }
}
