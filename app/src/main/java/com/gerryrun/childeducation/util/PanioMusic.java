package com.gerryrun.childeducation.util;


/**
 * 音乐播放帮助类
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.gerryrun.childeducation.R;

import java.util.HashMap;

public class PanioMusic {
    // 资源文件
//    int Music[] = {R.raw.note_c1, R.raw.note_c2, R.raw.note_c3, R.raw.note_c4, R.raw.note_c5,
//            R.raw.note_c6, R.raw.note_c7,R.raw.note_c8};
    int Music[] = {R.raw.white1, R.raw.white2, R.raw.white3, R.raw.white4, R.raw.white5,
            R.raw.white6, R.raw.white7, R.raw.white8};
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;

    public PanioMusic(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < Music.length; i++) {
            soundPoolMap.put(i, soundPool.load(context, Music[i], 1));
        }
    }

    public int soundPlay(int no) {
        return soundPool.play(soundPoolMap.get(no), 100, 100, 1, 0, 1.0f);
    }

    public int soundOver() {
        return soundPool.play(soundPoolMap.get(1), 100, 100, 1, 0, 1.0f);
    }

    @Override
    protected void finalize() throws Throwable {
        soundPool.release();
        super.finalize();
    }
}