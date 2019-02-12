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
import android.widget.ImageView;

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
            Log.w("Jerry", "当前进度: " + msg.what);
        }
    };

    private int duration;
    private MediaPlayer mediaPlayer;
    private AnimationsContainer.FramesSequenceAnimation mBgAnimation;

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
            handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                double currentTime = new BigDecimal((float) currentPosition / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (resultSequences.size() > 0) {
                    ResultSequence resultSequence = resultSequences.get(0);
                    if (currentTime > resultSequence.getCurrentTime()) {
                        Log.w("jerry", "特殊节点: " + currentTime + "  :  " + resultSequence.getCurrentTime() + "  状态: " + (resultSequence.isOpen() ? "打开" : "关闭"));
                        handler.sendEmptyMessage(currentPosition);
                        resultSequences.remove(resultSequence);
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.w("jerry", "run: 线程结束");
        }
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
