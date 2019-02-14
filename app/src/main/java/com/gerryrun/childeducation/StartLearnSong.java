package com.gerryrun.childeducation;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gerryrun.childeducation.parse.ReadMIDI;
import com.gerryrun.childeducation.parse.entity.ResultSequence;
import com.gerryrun.childeducation.util.AnimationsContainer;

import java.util.ArrayList;

public class StartLearnSong extends BaseActivity {

    public int screenSecond = 5000;
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
    private AnimationsContainer.FramesSequenceAnimation mBgAnimation;
    private FrameLayout flAddPitch;
    private Thread playerThread;
    private boolean isPlaying;
    private float pitchSpace = 5.3f;               //音符间距 占rightSpacePx宽度的比例 也就是说，基线右边屏准备最多放几个音符
    private float baselineScaling = 0.23f;         //绿色线位置在屏幕宽度的比例

    private float pitchMarginPx;      //标准一个音符的间距
    private float leftSpacePx;      //基线左边的像素个数
    private float rightSpacePx;     //基线右边的像素个数


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

    private synchronized void addPitch(ResultSequence resultSequence) {
        String pitch = resultSequence.getPitch();
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        imageView.setImageResource(getImagePitch(pitch));

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0.2f,
                Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
//        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                imageView.clearAnimation();
                flAddPitch.removeView(imageView);
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

    private int getImagePitch(String pitch) {
        pitch = pitch.substring(0, 1);
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
            Log.w("JerryZhu", "左边: " + leftSpacePx + "  右边: " + rightSpacePx + "  最终比例:" + pitchMarginPx);
        });

        ImageView imBg = findViewById(R.id.bg_im);
        mBgAnimation = AnimationsContainer.getInstance(R.array.bg_res, 120).createProgressDialogAnim(imBg);
        mBgAnimation.start();
    }

    private void initPlayer() {
        //将声音资源文件设置给MediaPlayer对象
        mediaPlayer = MediaPlayer.create(this, R.raw.small_start);
        playerThread = new Thread(new MusicThread());
        playerThread.start();
    }

    private void addPitch2(ArrayList<ResultSequence> resultSequences, ArrayList<ImageView> images) {
        Log.w("JerryZhu", "宽度: " + flAddPitch.getWidth());
        //不管关，只管按一下的情况，开的时间基本上就是关的时间

        for (int i = 0; i < resultSequences.size(); i += 2) {
            ResultSequence resultSequence = resultSequences.get(i);
            ImageView imageView = new ImageView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            int left = (int) (resultSequence.getCurrentTime() * pitchMarginPx + leftSpacePx);
//            Log.w("JerryZhu", "addPitch2 margin left : " + left);
            params.setMargins(left, 200, 0, 0);
            imageView.setLayoutParams(params);
            imageView.setImageResource(getImagePitch(resultSequence.getPitch()));
            flAddPitch.addView(imageView);
            images.add(imageView);
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
            mediaPlayer.setOnCompletionListener(mp -> isPlaying = false);
            mediaPlayer.start();
            isPlaying = true;
//            handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            ArrayList<ImageView> images = new ArrayList();
            runOnUiThread(() -> {
                addPitch2(resultSequences, images);
            });
            while (mediaPlayer != null && isPlaying) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    for (int i = 0; i < images.size(); i++) {
                        ImageView image = images.get(i);
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
//                        int left = layoutParams.leftMargin - 50;
                        int left = (int) (layoutParams.leftMargin - pitchMarginPx / (1000 / 50));
                        layoutParams.setMargins(left, 200, 0, 0);
                        image.setLayoutParams(layoutParams);
                        if (left < flAddPitch.getWidth() * baselineScaling) {
                            images.remove(i);
                            i--;
                            flAddPitch.removeView(image);
                        }
                    }
                });
            }
          /*  while (mediaPlayer != null && isPlaying) {
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
            }*/
        }
    }
}
