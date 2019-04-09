package com.gerryrun.childeducation.piano;

import android.Manifest.permission;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            requestPermissions(new String[]{permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    //节奏
    public void rhythm(View view) {
        startActivity(new Intent(this, Rhythm.class));
    }

    //学音高
    public void learnPitch(View view) {
        startActivity(new Intent(this, LearnPitch.class));
    }

    //竞猜
    public void Guess(View view) {
        startActivity(new Intent(this, Guess.class));
    }
}
