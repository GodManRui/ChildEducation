package com.gerryrun.childeducation.customview;

import android.content.Context;
import android.widget.ImageView;

public class PitchFactory {
    public static int cacheTime = 5000;  //预读 5s 的乐符
    public static int DUO = 1;
    public static int RE = 2;
    public static int MI = 3;
    public static int FA = 4;
    public static int SOL = 5;
    public static int LA = 6;
    public static int SI = 7;
    public static int DOL = 8;
    private PitchFactory pitchFactory;

    public PitchFactory getInstance() {
        synchronized (this) {
            if (pitchFactory == null) pitchFactory = new PitchFactory();
        }
        return pitchFactory;
    }

    public ImageView createPitch(int pitch, Context context) {
        ImageView imageView = new ImageView(context);
        return imageView;
    }

}
