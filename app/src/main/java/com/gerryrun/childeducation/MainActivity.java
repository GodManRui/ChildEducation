package com.gerryrun.childeducation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gerryrun.childeducation.util.StatusBarColor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarColor.setWindowsTranslucent(this);
        setContentView(R.layout.activity_main);
    }

    //节奏
    public void rhythm(View view) {
        startActivity(new Intent(this,Rhythm.class));
    }

    //学音高
    public void learnPitch(View view) {
        startActivity(new Intent(this,LearnPitch.class));
    }

    //竞猜
    public void Guess(View view) {
        startActivity(new Intent(this,Guess.class));

    }
}
