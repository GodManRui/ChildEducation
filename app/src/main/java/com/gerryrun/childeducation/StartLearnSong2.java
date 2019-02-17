package com.gerryrun.childeducation;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gerryrun.childeducation.parse.ReadMIDI;
import com.gerryrun.childeducation.parse.entity.ResultSequence;
import com.gerryrun.childeducation.util.AnimationsContainer;

import java.util.ArrayList;

import static com.gerryrun.childeducation.StartLearnSong.getImagePitch;

/**
 * 系统动画
 */
public class StartLearnSong2 extends BaseActivity {

    //运用Handler中的handleMessage方法接收子线程传递的信息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.w("jerry", "Handler: " + msg.what);
        }
    };
    private MediaPlayer mediaPlayer;
    private FrameLayout flAddPitch;
    private Thread playerThread;
    private boolean isPlaying;
    private float pitchSpace = 6.3f;               //音符间距 占rightSpacePx宽度的比例 也就是说，基线右边屏准备最多放几个音符
    private float baselineScaling = 0.23f;         //绿色线位置在屏幕宽度的比例

    private float pitchMarginPx;      //标准一个音符的间距
    private float leftSpacePx;      //基线左边的像素个数
    private float rightSpacePx;     //基线右边的像素个数
    private float dol;
    private float re;
    private float mi;
    private float fa;
    private float sol;
    private float la;
    private float si;
    private int pitchWH;
    private ImageView imageAnimationView;
    private int mBlastWH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_learn_song);
        initBackgroundAnim();
        initPlayer();
    }

    private synchronized void addPitch(ResultSequence resultSequence) {

        String pitch = resultSequence.getPitchNote();
//        float marginTop = getMarginTop(pitch) / flAddPitch.getHeight();

        ImageView imageView = new ImageView(StartLearnSong2.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pitchWH, pitchWH);
        params.topMargin = (int) getMarginTop(pitch);
        imageView.setTag(params.topMargin);
        imageView.setLayoutParams(params);
        imageView.setImageResource(getImagePitch(pitch));

            /*Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0.2f,
                    Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);*/
        float fromXValue = (float) (0.2f + resultSequence.getCurrentTime() * 0.1f);
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, fromXValue, Animation.RELATIVE_TO_PARENT, 0.2f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        long round = Math.round(resultSequence.getCurrentTime() * 1000);
        animation.setDuration(round);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                imageView.clearAnimation();
                removeAnimation(imageView);
//                    flAddPitch.removeView(imageView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animation.setInterpolator(new LinearInterpolator());
//        imageView.setVisibility(View.GONE);
        flAddPitch.addView(imageView);
        imageView.startAnimation(animation);

    }

    private void initBackgroundAnim() {
        flAddPitch = findViewById(R.id.fl_add_pitch);
        flAddPitch.post(() -> {
            leftSpacePx = flAddPitch.getWidth() * baselineScaling;
            rightSpacePx = flAddPitch.getWidth() * (1 - baselineScaling);
            pitchMarginPx = rightSpacePx / pitchSpace;
            Log.w("JerryZhu", "左边: " + leftSpacePx + "  右边: " + rightSpacePx + "  最终比例:" + pitchMarginPx);

            int height = flAddPitch.getHeight();
            pitchWH = (int) (height * 0.19f);
            dol = height * (1f - 0.19f);//*
            re = height * 0.73f;    //*
            mi = height * 0.62f;    //*
            fa = height * 0.54f;    //*
            sol = height * 0.43f;//*
            la = height * 0.35f; //*
            si = height * 0.25f;

            mBlastWH = pitchWH * 3;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mBlastWH, mBlastWH);
            params.leftMargin = (int) leftSpacePx - mBlastWH / 2;
            Log.w("jerry", "initBackgroundAnim: " + params.leftMargin);
            imageAnimationView = new ImageView(this);
            imageAnimationView.setLayoutParams(params);
            imageAnimationView.setVisibility(View.INVISIBLE);
            flAddPitch.addView(imageAnimationView);
        });

        ImageView imBg = findViewById(R.id.bg_im);
//        mBgAnimation = AnimationsContainer.getInstance(R.array.bg_res, 120).createProgressDialogAnim(imBg, false);
        AnimationsContainer mBgAnimation
                = new AnimationsContainer(R.array.bg_res, 30).createProgressDialogAnim(imBg, true);
        mBgAnimation.start();
    }

    private void initPlayer() {
        //将声音资源文件设置给MediaPlayer对象
        mediaPlayer = MediaPlayer.create(this, R.raw.small_start);
        playerThread = new Thread(new MusicThread());
        playerThread.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onMyDestroy();
    }

    private void onMyDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onMyDestroy();
    }

    private float getMarginTop(String pitchNote) {
        switch (pitchNote) {
            case "C":
                return dol;
            case "D":
                return re;
            case "E":
                return mi;
            case "F":
                return fa;
            case "G":
                return sol;
            case "A":
                return la;
            case "B":
                return si;
        }
        return 0;
    }


    class MusicThread implements Runnable {
        @Override
        public void run() {
            //todo 解析文件
            ReadMIDI readMIDI = new ReadMIDI();
            ArrayList<ResultSequence> resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.small_start));
            if (resultSequences == null) {
                Log.e("jerry", "run: 文件解析失败，可能不是标准的mid文件");
                return;
            }
            if (mediaPlayer == null) return;
            mediaPlayer.setOnCompletionListener(mp -> isPlaying = false);
            mediaPlayer.start();
            isPlaying = true;
            runOnUiThread(() -> {
                for (int i = 0; i < resultSequences.size(); i += 2) {
                    addPitch(resultSequences.get(i));
                }
            });

            ////////////////
        }
    }

    private void removeAnimation(ImageView image) {
        imageAnimationView.clearAnimation();
        if (imageAnimationView.getVisibility() != View.VISIBLE)
            imageAnimationView.setVisibility(View.VISIBLE);
        if (imageAnimationView.getTag() != null) {
            AnimationsContainer progressDialogAnim = (AnimationsContainer) imageAnimationView.getTag();
            progressDialogAnim.stop();
            progressDialogAnim = null;
        }
        FrameLayout.LayoutParams animationParams = (FrameLayout.LayoutParams) imageAnimationView.getLayoutParams();
        animationParams.topMargin = (int) image.getTag() - (mBlastWH / 2 - pitchWH / 2);
        imageAnimationView.setLayoutParams(animationParams);
        flAddPitch.removeView(image);
        AnimationsContainer progressDialogAnim = new AnimationsContainer(R.array.music_orange, 60).createProgressDialogAnim(imageAnimationView, false);
        progressDialogAnim.start();

    }
}
