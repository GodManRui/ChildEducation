package com.gerryrun.childeducation;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gerryrun.childeducation.parse.ReadMIDI;
import com.gerryrun.childeducation.parse.entity.ResultSequence;
import com.gerryrun.childeducation.util.DensityUtil;

import java.util.ArrayList;

import static tech.linjiang.pandora.util.ViewKnife.px2dip;

public class Rhythm extends BaseActivity {

    private View imYuePu;
    private View vIndicator;
    private ArrayList<ResultSequence> resultSequences;
    private long startPlayTimeMillis;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rhythm);
        initView();
        initPlayer();
    }

    private void initPlayer() {
        ReadMIDI readMIDI = new ReadMIDI();
        resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.jiequ));
        if (resultSequences == null) {
            Log.e("jerry", "run: 文件解析失败，可能不是标准的mid文件");
            return;
        }
        for (ResultSequence resultSequence : resultSequences) {
            Log.e("jerry", "initPlayer: " + resultSequence.toString());
        }
    }

    private void initView() {
        imYuePu = findViewById(R.id.im_yue_pu);
        vIndicator = findViewById(R.id.v_indicator);
        imYuePu.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vIndicator.getLayoutParams();
            layoutParams.leftMargin = imYuePu.getLeft();
            layoutParams.height = imYuePu.getHeight();
            layoutParams.topMargin = imYuePu.getTop();
            vIndicator.setLayoutParams(layoutParams);
        });
        findViewById(R.id.rl_select_song).setOnClickListener((v) -> {
            v.setVisibility(View.GONE);
            imYuePu.setVisibility(View.VISIBLE);
            startPlay();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        resultSequences.clear();
    }

    private void startPlay() {
        float width = imYuePu.getWidth() * (1 - 0.062f);
        float startPx = imYuePu.getLeft() + imYuePu.getWidth() * 0.062f;
        mediaPlayer = MediaPlayer.create(this, R.raw.jiequ);
        int duration = mediaPlayer.getDuration();
        Log.e("jerry", "startPlay: " + " 开始偏移量: " + px2dip(startPx) + " 宽度：" + px2dip(width) + "  时间:" + duration);
        new Thread(() -> {
            while (resultSequences.size() > 0) {
                long currentPlay = System.currentTimeMillis() - startPlayTimeMillis;
                if (currentPlay > duration) continue;
                double currentNoteTime = resultSequences.get(0).getCurrentTime() * 1000;
                if (currentPlay > currentNoteTime) {
                    double marginLeft = startPx + width * (currentNoteTime / duration);
                    Log.e("jerry", "大于了: 节点 " + currentNoteTime + " 当前：" + ((double) currentPlay) + "ms  marleft: " + px2dip((int) marginLeft) + "   int：" + (int) marginLeft);
                    runOnUiThread(() -> {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vIndicator.getLayoutParams();
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
        }).start();
        mediaPlayer.start();
        startPlayTimeMillis = System.currentTimeMillis();
    }
}
