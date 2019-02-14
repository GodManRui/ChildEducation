package com.gerryrun.childeducation;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gerryrun.childeducation.parse.ReadMIDI;
import com.gerryrun.childeducation.parse.entity.ResultSequence;
import com.gerryrun.childeducation.util.AnimationsContainer;

import java.math.BigDecimal;
import java.util.ArrayList;

public class StartLearnSong extends BaseActivity {

    private View llStartLearn;
    private AnimationDrawable frameAnim;
    //    private ImageView imMoon;
//    private AnimationDrawable frameAnimMoon;
    //运用Handler中的handleMessage方法接收子线程传递的信息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.w("jerry", "Handler: " + msg.what);
        }
    };

    private int duration;
    private MediaPlayer mediaPlayer;
    private AnimationsContainer.FramesSequenceAnimation mBgAnimation;
    private FrameLayout flAddPitch;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
            mediaPlayer.start();
            handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            mediaPlayer.isPlaying();
//            Log.w("jerry", "start: " + mediaPlayer.isPlaying());
            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                double currentTime = new BigDecimal((float) currentPosition / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (resultSequences.size() > 0) {
                    ResultSequence resultSequence = resultSequences.get(0);
                    if (currentTime > resultSequence.getCurrentTime()) {
                        Log.w("jerry", "node: " + currentTime + "  :  " + resultSequence.getCurrentTime() + "  Status: " + (resultSequence.isOpen() ? " Open " : "Close"));
//                        handler.sendEmptyMessage(currentPosition);
                        flAddPitch.post(() -> {
                            addPitch(resultSequence);
                            resultSequences.remove(resultSequence);
                        });
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.w("jerry", "run: Thread END" + mediaPlayer.isPlaying());
        }
    }

    private synchronized void addPitch(ResultSequence resultSequence) {
        String pitch = resultSequence.getPitch();
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        imageView.setImageResource(getImagePitch(pitch));
        flAddPitch.addView(imageView);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private int getImagePitch(String pitch) {
        pitch = pitch.substring(0,1);
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
        Log.w("jerry", "getImagePitch: " + pitch);
        return R.drawable.yyqijian12;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_learn_song);
        initAnim();
        initPlayer();
    }

    private void initPlayer() {
        //将声音资源文件设置给MediaPlayer对象
        mediaPlayer = MediaPlayer.create(this, R.raw.small_start);
        duration = mediaPlayer.getDuration();
        Log.w("Jerry", "initPlayer: " + duration);
        new Thread(new MusicThread()).start();
    }

    private void initAnim() {
        llStartLearn = findViewById(R.id.ll_start_learn_bg);
        flAddPitch = findViewById(R.id.fl_add_pitch);
        ImageView imBg = findViewById(R.id.bg_im);
        mBgAnimation = AnimationsContainer.getInstance(R.array.bg_res, 120).createProgressDialogAnim(imBg);
        mBgAnimation.start();
//        imMoon = findViewById(R.id.im_moon);
        // 通过逐帧动画的资源文件获得AnimationDrawable示例
       /* frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.bg_start_learn_song);
//        frameAnimMoon = (AnimationDrawable) getResources().getDrawable(R.drawable.bg_moon);
        // 把AnimationDrawable设置为ImageView的背景
        llStartLearn.setBackground(frameAnim);
//        imMoon.setImageDrawable(frameAnimMoon);
        frameAnim.start();*/
//        frameAnimMoon.start();
    }
}
