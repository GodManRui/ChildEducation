package com.gerryrun.childeducation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gerryrun.childeducation.util.AnimationsContainer;
import com.gerryrun.childeducation.util.StatusBarColor;

public class Rhythm extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(imageView);
        setContentView(linearLayout);
        setTitle("节奏");
        AnimationsContainer  progressDialogAnim = new AnimationsContainer(R.array.music_orange, 60).createProgressDialogAnim(imageView, false);
//        AnimationsContainer.getInstance(R.array.music_orange, 60).createProgressDialogAnim(image,false);
        progressDialogAnim.setOnAnimStopListener(() -> {
            Log.w("jerry", "动画停止: ");
            linearLayout.removeView(imageView);
            progressDialogAnim.stop();
        });
        progressDialogAnim.start();
    }
}
