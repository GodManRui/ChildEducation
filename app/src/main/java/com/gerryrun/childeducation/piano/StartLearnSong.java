package com.gerryrun.childeducation.piano;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gerryrun.childeducation.piano.R;

import com.gerryrun.childeducation.piano.parse.ReadMIDI;
import com.gerryrun.childeducation.piano.parse.entity.ResultSequence;
import com.gerryrun.childeducation.piano.util.AnimationsContainer;

import java.util.ArrayList;

/**
 * 自定义动画
 */
public class StartLearnSong extends BaseActivity {

    //运用Handler中的handleMessage方法接收子线程传递的信息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

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
    private final int FPS = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_learn_song);
        initBackgroundAnim();
        initPlayer();
    }

    private void initBackgroundAnim() {
        flAddPitch = findViewById(R.id.fl_add_pitch);
        flAddPitch.post(() -> {
            leftSpacePx = flAddPitch.getWidth() * baselineScaling;
            rightSpacePx = flAddPitch.getWidth() * (1 - baselineScaling);
            pitchMarginPx = rightSpacePx / pitchSpace;


            int height = flAddPitch.getHeight();
            pitchWH = (int) (height * 0.19f);
            dol = height * (1f - 0.19f);//*
            re = height * 0.73f;    //*
            mi = height * 0.62f;    //*
            fa = height * 0.54f;    //*
            sol = height * 0.43f;//*
            la = height * 0.35f; //*
            si = height * 0.25f;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pitchWH, pitchWH);
            imageAnimationView = new ImageView(this);
            imageAnimationView.setLayoutParams(params);
            imageAnimationView.setVisibility(View.INVISIBLE);
            flAddPitch.addView(imageAnimationView);
        });

//        ImageView imBg = findViewById(R.id.bg_im);
//        mBgAnimation = AnimationsContainer.getInstance(R.array.bg_res, 120).createProgressDialogAnim(imBg, false);
       /* AnimationsContainer mBgAnimation
                = new AnimationsContainer(R.array.bg_res, 20).createProgressDialogAnim(imBg, true);
        mBgAnimation.start();*/
    }

    private void initPlayer() {
        //将声音资源文件设置给MediaPlayer对象
        mediaPlayer = MediaPlayer.create(this, R.raw.elise);
        playerThread = new Thread(new MusicThread());
        playerThread.start();
    }

    private void addPitch2(ArrayList<ResultSequence> resultSequences, ArrayList<ImageView> images) {
        //创建音符
        //不管关，只管按一下的情况，开的时间基本上就是关的时间
        for (int i = 0; i < resultSequences.size(); i += 2) {
            ResultSequence resultSequence = resultSequences.get(i);
            ImageView imageView = new ImageView(this);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pitchWH,
                    pitchWH);
            int left = (int) (resultSequence.getCurrentTime() * pitchMarginPx + leftSpacePx);

            float marginTop = getMarginTop(resultSequence.getPitchNote());
            params.setMargins(left, (int) marginTop, 0, 0);
            imageView.setLayoutParams(params);
            int imageResource = getImagePitch(resultSequence.getPitchNote());
            imageView.setImageResource(imageResource);
            imageView.setTag(marginTop);
            flAddPitch.addView(imageView);
            images.add(imageView);
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

    public static int getImagePitch(String pitch) {
        switch (pitch) {
            case "C":
                return R.drawable.yyqijian12;
            case "D":
                return R.drawable.yyqijian14;
            case "E":
                return R.drawable.yyqijian19;
            case "F":
                return R.drawable.yyqijian18;
            case "G":
                return R.drawable.yyqijian17;
            case "A":
                return R.drawable.yyqijian16;
            case "B":
                return R.drawable.yyqijian13;
        }
        return R.drawable.yyqijian12;
    }


    class MusicThread implements Runnable {
        @Override
        public void run() {
            //todo 解析文件
            ReadMIDI readMIDI = new ReadMIDI();
            ArrayList<ResultSequence> resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.elise));
            if (resultSequences == null) {

                return;
            }
            if (mediaPlayer == null) return;
            mediaPlayer.setOnCompletionListener(mp -> isPlaying = false);
            mediaPlayer.start();
            isPlaying = true;
//            handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            ArrayList<ImageView> images = new ArrayList();
            runOnUiThread(() -> {
                //创建音符
                addPitch2(resultSequences, images);
            });

            //移动音符
            while (mediaPlayer != null && isPlaying) {
                runOnUiThread(() -> {
                    //移动
                    for (int i = 0; i < images.size(); i++) {
                        ImageView image = images.get(i);
                        image.clearAnimation();
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
//                        int left = layoutParams.leftMargin - 50;
                        int left = (int) (layoutParams.leftMargin - pitchMarginPx / (1000 / FPS));

                        if (left < flAddPitch.getWidth() * baselineScaling) {
                            removeAnimation(image);
                            images.remove(i);
                            i--;
//                            flAddPitch.removeView(image);
                        } else {
                            layoutParams.setMargins(left, (int) (float) image.getTag(), 0, 0);
                            image.setLayoutParams(layoutParams);
                        }
                    }
                });
                try {
                    Thread.sleep(FPS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void removeAnimation(ImageView image) {
        imageAnimationView.clearAnimation();
        if (imageAnimationView.getVisibility() != View.VISIBLE)
            imageAnimationView.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        FrameLayout.LayoutParams animationParams = (FrameLayout.LayoutParams) imageAnimationView.getLayoutParams();
        animationParams.leftMargin = layoutParams.leftMargin;
        animationParams.topMargin = layoutParams.topMargin;
        imageAnimationView.setLayoutParams(animationParams);
        flAddPitch.removeView(image);
        AnimationsContainer progressDialogAnim = new AnimationsContainer(R.array.music_orange, 60).createProgressDialogAnim(imageAnimationView, false);
        progressDialogAnim.start();
        /*AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.animalist);
        imageAnimationView.setBackground(animationDrawable);
        animationDrawable.start();*/

//        AnimationDrawable animationDrawable = (AnimationDrawable) image.getDrawable();

  /*      int duration = 0;
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
            duration += animationDrawable.getDuration(i);
        }
        handler.postDelayed(() -> {

            //此处调用 第二个动画播放方法
            flAddPitch.removeView(image);
        }, duration);
        animationDrawable.start();*/

//        flAddPitch.removeView(image);

//        image.setImageResource(0);


//        image.setImageResource(R.drawable.animalist);
      /*  FrameAnimation frameAnimation = new FrameAnimation(image, getRes(), 50, false);
        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                image.setImageResource(0);
                image.setBackgroundResource(0);
                flAddPitch.removeView(image);
                Log.w("jerry", "onAnimationEnd: " );

            }

            @Override
            public void onAnimationRepeat() {
                Log.w("jerry", "onAnimationRepeat: " );

            }
        });*/


       /* AnimationsContainer.FramesSequenceAnimation progressDialogAnim = new AnimationsContainer(R.array.music_orange, 60).createProgressDialogAnim(image, false);
//        AnimationsContainer.getInstance(R.array.music_orange, 60).createProgressDialogAnim(image,false);
        progressDialogAnim.setOnAnimStopListener(() -> {
            flAddPitch.removeView(image);
            Log.w("jerry", "动画停止: ");
//            progressDialogAnim.stop();
        });
        progressDialogAnim.start();*/
    }

    public static int getBearArrays(int resourceId) {
        switch (resourceId) {
            case R.drawable.yyqijian12:
                return R.array.music_bear_do;
            case R.drawable.yyqijian14:
                return R.array.music_bear_re;
            case R.drawable.yyqijian19:
                return R.array.music_bear_mi;
            case R.drawable.yyqijian18:
                return R.array.music_bear_fa;
            case R.drawable.yyqijian17:
                return R.array.music_bear_sol;
            case R.drawable.yyqijian16:
                return R.array.music_bear_la;
            case R.drawable.yyqijian13:
                return R.array.music_bear_si;
        }
        return R.array.music_bear_nomal;
    }

    public static int getFrameArrays(int resourceId) {
        switch (resourceId) {
            case R.drawable.yyqijian12:
                return R.array.music_red;
            case R.drawable.yyqijian14:
                return R.array.music_orange;
            case R.drawable.yyqijian19:
                return R.array.music_yellow;
            case R.drawable.yyqijian18:
                return R.array.music_green;
            case R.drawable.yyqijian17:
                return R.array.music_blue;

            //la si 没给爆炸图
            case R.drawable.yyqijian16:
            case R.drawable.yyqijian13:
                return R.array.music_orange;
        }
        return 0;
    }
}
