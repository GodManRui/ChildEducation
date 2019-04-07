package com.gerryrun.childeducation.piano;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.gerryrun.childeducation.piano.parse.ReadMIDI;
import com.gerryrun.childeducation.piano.parse.entity.ResultSequence;

import java.util.ArrayList;

public class Rhythm extends BaseActivity {

    private View imYuePu;
    private View vIndicator;
    private View vIndicator2;
    private ArrayList<ResultSequence> resultSequences;
    private long startPlayTimeMillis;
    //    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int load;
    private float playRate = 1f;
    private ImageView imIndicator;
    private ImageView imPaiZi;
    private ArrayList<ResultSequence> saveResultSequences;
    private int duration;
    private Thread playThread;
    private int shouldStop = 2;
    private View selectView;
    private ImageView imPlayPause;
    /**
     * 0未播放  1 播放中  2暂停
     */
    private int soundPoolPlayState;
    private ProgressDialog progressDialog;
    //    private boolean isPlaying;
//    private boolean firstPlay = true;

    public static int px2dip(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rhythm);
        initView();
        initPlayer();
    }

    private void initView() {
        imYuePu = findViewById(R.id.im_yue_pu);
        vIndicator = findViewById(R.id.v_indicator);
        vIndicator2 = findViewById(R.id.v_indicator2);
        imIndicator = findViewById(R.id.im_jie_pai_indicator);
        imPlayPause = findViewById(R.id.im_rhythm_play_pause);
        imPlayPause.setOnClickListener(v -> {
            if (soundPool != null) {

                if (soundPoolPlayState == 0) { //停止播放
                    soundPoolPlayState = 1;
                    imPlayPause.setImageResource(R.drawable.pause);
                    clickStopPlay();
                } else if (soundPoolPlayState == 1) {   //播放中
                    soundPool.pause(load);
                    soundPoolPlayState = 2;
                    imPlayPause.setImageResource(R.drawable.play);
                } else if (soundPoolPlayState == 2) {   //暂停中
                    soundPool.resume(load);
                    soundPoolPlayState = 1;
                    imPlayPause.setImageResource(R.drawable.pause);
                }
            }
        });
        findViewById(R.id.tv_song_name).setOnClickListener(v -> {
            selectView.setVisibility(View.GONE);
        });

        findViewById(R.id.im_ge_dan).setOnClickListener(v -> {
            selectView.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.im_return).setOnClickListener(v -> {
            soundDestroy(false);
            finish();
        });

        selectView = findViewById(R.id.rl_select_song);
        selectView.setOnClickListener(v -> {
        });
        findViewById(R.id.im_select_close).setOnClickListener((v) -> {
            selectView.setVisibility(selectView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });
        findViewById(R.id.im_jiepaiqi).setOnClickListener((v) -> {
            //点击节拍器,切换快慢拍

            int nextBeatRes = getNextBeatRes();
            imPaiZi.setImageResource(nextBeatRes);
            imPaiZi.setTag(nextBeatRes);

            if (playThread != null) {
                shouldStop = 1;
                playThread.interrupt();
                playThread = null;
            }
            resetIndicator();
        });
        imPaiZi = findViewById(R.id.im_paizi);
        imPaiZi.setTag(R.drawable.music_jiepaiqi_zhongpai_1);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("资源准备中...");
        progressDialog.setCancelable(false);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.jiequ);
        duration = mediaPlayer.getDuration();
    }

    private void initPlayer() {
        ReadMIDI readMIDI = new ReadMIDI();
        resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.jiequ));
        if (resultSequences == null) {

            return;
        }
        saveResultSequences = new ArrayList<>(resultSequences.size());
        saveResultSequences.addAll(this.resultSequences);
        for (ResultSequence resultSequence : this.resultSequences) {

        }
        soundPool = new SoundPool(18, AudioManager.STREAM_MUSIC, 100);
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        });
        load = soundPool.load(this, R.raw.jiequ, 1);
        if (progressDialog != null) progressDialog.show();
    }

    private void startPlay(float duration) {
        float startPx = imYuePu.getWidth() * 0.255f;
        float width = imYuePu.getWidth() * 0.6046f;
        resultSequences.clear();
        resultSequences.addAll(saveResultSequences);
        shouldStop = 0;
        vIndicator.setVisibility(View.VISIBLE);
        vIndicator2.setVisibility(View.VISIBLE);
        playThread = new Thread(() -> {
            load = soundPool.play(load, 1, 1, 1, 0, playRate);
            startPlayTimeMillis = System.currentTimeMillis();
            float currentProgress = 0;
            while (resultSequences.size() > 0 && shouldStop == 0) {
                if (soundPoolPlayState == 2) {
                    if (currentProgress == 0) {
                        currentProgress = (System.currentTimeMillis() - startPlayTimeMillis) * playRate;

                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                float currentPlay;
                if (currentProgress != 0) {
                    currentPlay = currentProgress + 10;
                    long current = System.currentTimeMillis();
                    long diff = Float.valueOf((currentProgress / playRate) + "").longValue();
                    startPlayTimeMillis = current - diff;
                    currentProgress = 0;
                } else
                    currentPlay = (System.currentTimeMillis() - startPlayTimeMillis) * playRate;

                if (currentPlay > 6520 || currentPlay > duration) {
                    resultSequences.clear();
                    break;
                }
                double currentNoteTime = resultSequences.get(0).getCurrentTime() * 1000;
                if (currentPlay > currentNoteTime) {
                    double marginLeft = startPx + width * (currentNoteTime / 5420);

                    runOnUiThread(() -> {
                        LayoutParams layoutParams = (LayoutParams) vIndicator.getLayoutParams();
                        layoutParams.leftMargin = (int) marginLeft;
                        vIndicator.setLayoutParams(layoutParams);
                        LayoutParams layoutParams2 = (LayoutParams) vIndicator2.getLayoutParams();
                        layoutParams2.leftMargin = (int) marginLeft;
                        vIndicator2.setLayoutParams(layoutParams2);
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
            soundDestroy(false);
            shouldStop = 2;
            playThread = null;
        });
        playThread.start();
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
            playThread.interrupt();
            playThread = null;
        }
        if (resetIndicator()) return;
        startPlay(duration * playRate);
        initAnimation();
    }

    private boolean resetIndicator() {
        if (vIndicator == null) return true;
        if (vIndicator2 == null) return true;
        LayoutParams layoutParams = (LayoutParams) vIndicator.getLayoutParams();
        if (layoutParams == null) return true;
        layoutParams.leftMargin = 0;
        vIndicator.setLayoutParams(layoutParams);

        LayoutParams layoutParams2 = (LayoutParams) vIndicator2.getLayoutParams();
        if (layoutParams2 == null) return true;
        layoutParams2.leftMargin = 0;
        vIndicator2.setLayoutParams(layoutParams2);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundDestroy(true);
        resultSequences.clear();
    }

    private void soundDestroy(boolean isFinish) {
        try {
            soundPoolPlayState = 0;
            runOnUiThread(() -> {
                vIndicator.setVisibility(View.INVISIBLE);
                vIndicator2.setVisibility(View.INVISIBLE);
                if (progressDialog != null && isFinish)
                    progressDialog.show();
                if (soundPool != null) {
                    soundPool.stop(load);
                    if (isFinish) {
                        soundPool.release();
                        soundPool = null;
                    } else {
                        soundPool.unload(load);
                        load = soundPool.load(this, R.raw.jiequ, 1);
                    }
                }
                if (!isFinish)
                    imPlayPause.setImageResource(R.drawable.play);
                Animation animation = imIndicator.getAnimation();
                imIndicator.clearAnimation();
                if (animation == null) return;
                animation.setAnimationListener(null);
            });
        } catch (Exception ignore) {
        }
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
}
