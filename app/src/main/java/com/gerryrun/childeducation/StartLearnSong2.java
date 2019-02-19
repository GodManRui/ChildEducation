package com.gerryrun.childeducation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

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
    private ImageView imPlayPause;
    private ArrayList<ResultSequence> resultSequences;
    private ArrayList<ImageView> imagePitchViews;
    private boolean resetPlay = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_learn_song);
        initBackgroundAnim();
        initPlayer();
    }

    private void initBackgroundAnim() {
        flAddPitch = findViewById(R.id.fl_add_pitch);
        imPlayPause = findViewById(R.id.im_play_pause);
        imPlayPause.setOnClickListener((v) -> {
            clickPlayPause();
        });
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

    private void clickPlayPause() {
        if (imagePitchViews == null || imagePitchViews.size() <= 0) {
            Toast.makeText(this, "正在初始化,请稍后", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPlaying) {  //播放状态 ，暂停播放
            imPlayPause.setImageResource(R.drawable.yyqijian29);
            for (ImageView imagePitchView : imagePitchViews) {
                ObjectAnimator translationX = (ObjectAnimator) imagePitchView.getTag();
                translationX.pause();
            }
            isPlaying = false;
            mediaPlayer.pause();
        } else { //暂停状态， 开始播放
            imPlayPause.setImageResource(R.drawable.yyqijian28);
            for (ImageView imagePitchView : imagePitchViews) {
                ObjectAnimator translationX = (ObjectAnimator) imagePitchView.getTag();
                if (resetPlay) {             //如果是从头播放的
                    if (imagePitchView.getParent() == null)
                        flAddPitch.addView(imagePitchView);
                    translationX.start();
                    continue;
                }
                translationX.resume();
            }
            if (resetPlay) resetPlay = false;
            isPlaying = true;
//            if (resetPlay)
            mediaPlayer.start();
        }
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

    @UiThread
    private void preparePlay() {
        if (mediaPlayer == null) return;
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            resetPlay = true;
        });
        imagePitchViews = new ArrayList<>();
        for (int i = 0; i < resultSequences.size(); i += 2) {
            createImagePitch(resultSequences.get(i));
        }
    }

    private synchronized void createImagePitch(ResultSequence resultSequence) {

        String pitch = resultSequence.getPitchNote();
//        float marginTop = getMarginTop(pitch) / flAddPitch.getHeight();

        ImageView imageView = new ImageView(StartLearnSong2.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pitchWH, pitchWH);
        params.topMargin = (int) getMarginTop(pitch);
//        imageView.setTag(params.topMargin);
        imageView.setLayoutParams(params);
        int imagePitchSource = getImagePitch(pitch);
        imageView.setImageResource(imagePitchSource);
        imageView.setTag(R.id.tag_resource_id, imagePitchSource);

            /*Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0.2f,
                    Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);*/
        float fromXValue = (float) (baselineScaling + resultSequence.getCurrentTime() * 0.1f);
    /*    Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, fromXValue, Animation.RELATIVE_TO_PARENT, 0.2f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);*/
        ObjectAnimator translationX = ObjectAnimator.ofFloat(imageView, "translationX", flAddPitch.getWidth() * fromXValue, flAddPitch.getWidth() * baselineScaling);
        long round = Math.round(resultSequence.getCurrentTime() * 1000);
        translationX.setInterpolator(new LinearInterpolator());
        translationX.setDuration(round);
        translationX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeAnimation(imageView);
            }
        });
        imageView.setTag(translationX);
        imagePitchViews.add(imageView);
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
        animationParams.topMargin = ((FrameLayout.LayoutParams) image.getLayoutParams()).topMargin - (mBlastWH / 2 - pitchWH / 2);
        imageAnimationView.setLayoutParams(animationParams);
        AnimationsContainer progressDialogAnim = new AnimationsContainer(getFrameArrays((int) image.getTag(R.id.tag_resource_id)), 60).createProgressDialogAnim(imageAnimationView, false);
        flAddPitch.removeView(image);
        progressDialogAnim.start();

    }

    private int getFrameArrays(int resourceId) {
        switch (resourceId) {
            case R.drawable.yyqijian12:
                return R.array.music_orange;
            case R.drawable.yyqijian14:
                break;
            case R.drawable.yyqijian19:
                break;
            case R.drawable.yyqijian18:
                break;
            case R.drawable.yyqijian17:
                break;
            case R.drawable.yyqijian16:
                break;
            case R.drawable.yyqijian13:
                break;
        }
        return 0;
    }

    class MusicThread implements Runnable {
        @Override
        public void run() {
            //todo 解析文件
            ReadMIDI readMIDI = new ReadMIDI();
            resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.small_start));
            if (resultSequences == null) {
                Log.e("jerry", "run: 文件解析失败，可能不是标准的mid文件");
                return;
            }
            runOnUiThread(() -> preparePlay());
        }
    }
}
