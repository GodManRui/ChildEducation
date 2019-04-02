package com.gerryrun.childeducation.piano;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.parse.ReadMIDI;
import com.gerryrun.childeducation.piano.parse.entity.ResultSequence;

import java.util.ArrayList;

public class Rhythm extends BaseActivity {

    private View imYuePu;
    private View vIndicator;
    private ArrayList<ResultSequence> resultSequences;
    private long startPlayTimeMillis;
    //    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int load;
    private float playRate = 1f;
    private ImageView imIndicator;
    private ImageView imPaiZi;
    private View rlContorl;
    private ArrayList<ResultSequence> saveResultSequences;
    private int duration;
    private Thread playThread;
    private int shouldStop = 2;

    public static int px2dip(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rhythm);
        initView();
        initPlayer();
    }

    private void initView() {
        imYuePu = findViewById(R.id.im_yue_pu);
        vIndicator = findViewById(R.id.v_indicator);
        imIndicator = findViewById(R.id.im_jie_pai_indicator);
        rlContorl = findViewById(R.id.rl_crontor);

        findViewById(R.id.im_return).setOnClickListener(v -> {
            finish();
        });
        imYuePu.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vIndicator.getLayoutParams();
            layoutParams.leftMargin = imYuePu.getLeft();
            layoutParams.height = imYuePu.getHeight();
            layoutParams.topMargin = imYuePu.getTop();
            vIndicator.setLayoutParams(layoutParams);
        });
        findViewById(R.id.rl_select_song).setOnClickListener((v) -> {
            v.setVisibility(View.GONE);
            vIndicator.setVisibility(View.VISIBLE);
            imYuePu.setVisibility(View.VISIBLE);
            rlContorl.setVisibility(View.VISIBLE);
//            startPlay();
            startPlay2();
        });
        findViewById(R.id.im_jiepaiqi).setOnClickListener((v) -> {
            //点击节拍器,切换快慢拍
            int nextBeatRes = getNextBeatRes();
            imPaiZi.setImageResource(nextBeatRes);
            imPaiZi.setTag(nextBeatRes);
            clickStopPlay();
        });
        imPaiZi = findViewById(R.id.im_paizi);
        imPaiZi.setTag(R.drawable.music_jiepaiqi_zhongpai_1);
    }

    private void initPlayer() {
        ReadMIDI readMIDI = new ReadMIDI();
        resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.jiequ));
        if (resultSequences == null) {
            Log.e("jerry", "run: 文件解析失败，可能不是标准的mid文件");
            return;
        }
        saveResultSequences = new ArrayList<>(resultSequences.size());
        saveResultSequences.addAll(this.resultSequences);
        for (ResultSequence resultSequence : this.resultSequences) {
            Log.e("jerry", "initPlayer: " + resultSequence.toString());
        }
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);
        load = soundPool.load(this, R.raw.jiequ, 1);


        Toast.makeText(this, " = " + load, Toast.LENGTH_SHORT).show();
    }

    private void startPlay2() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.jiequ);
        duration = mediaPlayer.getDuration();
        startPlay(duration * playRate);
        initAnimation();
    }

    public int getNextBeatRes() {
        if (imPaiZi == null) return R.drawable.music_jiepaiqi_zhongpai_1;
        int currentID = (int) imPaiZi.getTag();
        switch (currentID) {
            case R.drawable.music_jiepaiqi_kuaipai_1:
                playRate = 0.5f;
                imIndicator.setImageResource(R.drawable.music_jiepaiqi_manpa);
                return R.drawable.music_jiepaiqi_manpai_1;
            case R.drawable.music_jiepaiqi_zhongpai_1:
                playRate = 1.4f;
                imIndicator.setImageResource(R.drawable.music_jiepaiqi_kuaipa);
                return R.drawable.music_jiepaiqi_kuaipai_1;
            case R.drawable.music_jiepaiqi_manpai_1:
                playRate = 1f;
                imIndicator.setImageResource(R.drawable.music_jiepaiqi_zhongpa);
                return R.drawable.music_jiepaiqi_zhongpai_1;
        }
        return R.drawable.music_jiepaiqi_zhongpai_1;
    }

    private void clickStopPlay() {
        if (playThread != null) {
            shouldStop = 1;
//            playThread.stop();
            playThread.interrupt();
            playThread = null;
        }
//        soundDestroy();
        if (vIndicator == null) return;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vIndicator.getLayoutParams();
        if (layoutParams == null) return;
        layoutParams.leftMargin = 0;
        vIndicator.setLayoutParams(layoutParams);

       /* while (shouldStop != 2) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }*/
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在切换节拍...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            startPlay(duration * playRate);
            initAnimation();
        }, 3000);
    }

    private void startPlay(float duration) {
        float startPx = imYuePu.getWidth() * 0.26f;
        float width = imYuePu.getWidth() * 0.6046f;
        resultSequences.clear();
        resultSequences.addAll(saveResultSequences);
        shouldStop = 0;
        playThread = new Thread(() -> {

            load = soundPool.play(load, 1, 1, 1, 0, playRate);

            startPlayTimeMillis = System.currentTimeMillis();
            while (resultSequences.size() > 0 && shouldStop == 0) {
                float currentPlay = (System.currentTimeMillis() - startPlayTimeMillis) * playRate;

                if (currentPlay > 6520 || currentPlay > duration) {
                    resultSequences.clear();
                    break;
                }
                double currentNoteTime = resultSequences.get(0).getCurrentTime() * 1000;
                if (currentPlay > currentNoteTime) {
                    double marginLeft = startPx + width * (currentNoteTime / 5420);
//                    Log.e("jerry", "大于了: 节点 " + currentNoteTime + " 当前：" + ((double) currentPlay) + "ms  marleft: " + px2dip((int) marginLeft) + "   int：" + (int) marginLeft);
                    runOnUiThread(() -> {
                        LayoutParams layoutParams = (LayoutParams) vIndicator.getLayoutParams();
                        layoutParams.leftMargin = (int) marginLeft;
                        vIndicator.setLayoutParams(layoutParams);
                    });
                    resultSequences.remove(0);
                    resultSequences.remove(0);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            soundDestroy();
            shouldStop = 2;
            playThread = null;
        });
        playThread.start();
    }

    private void initAnimation() {
        int duration = 0;
        if (playRate > 1.0f) {
            duration = 535;
        } else if (playRate < 1.0f) {
            duration = 1250;
        } else {
            duration = 833;
        }
        RotateAnimation rotate = new RotateAnimation(-20f, 20f, Animation.RELATIVE_TO_SELF, 0.6f, Animation.RELATIVE_TO_SELF, 1f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(duration);  //设置动画持续周期
        rotate.setFillAfter(true); //动画执行完后是否停留在执行完的状态
        RotateAnimation rotateReturn = new RotateAnimation(20f, -20f, Animation.RELATIVE_TO_SELF, 0.6f, Animation.RELATIVE_TO_SELF, 1f);
        LinearInterpolator lin2 = new LinearInterpolator();
        rotateReturn.setInterpolator(lin2);
        rotateReturn.setDuration(duration);  //设置动画持续周期
        rotateReturn.setFillAfter(true); //动画执行完后是否停留在执行完的状态
        rotateReturn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imIndicator.clearAnimation();
                rotate.start();
                imIndicator.setAnimation(rotate);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imIndicator.clearAnimation();
                rotateReturn.start();
                imIndicator.setAnimation(rotateReturn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imIndicator.setAnimation(rotate);
        imIndicator.startAnimation(rotate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundDestroy();
        resultSequences.clear();
    }

    private void soundDestroy() {
        if (soundPool != null) {
            soundPool.stop(load);
            soundPool.unload(load);
            load = soundPool.load(this, R.raw.jiequ, 1);

        }
        runOnUiThread(() -> {
            Animation animation = imIndicator.getAnimation();
            if (animation == null) return;

            imIndicator.clearAnimation();
            animation.setAnimationListener(null);
        });
    }
}
