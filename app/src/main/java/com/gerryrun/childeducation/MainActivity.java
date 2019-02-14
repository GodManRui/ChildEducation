package com.gerryrun.childeducation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gerryrun.childeducation.util.StatusBarColor;

public class MainActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
