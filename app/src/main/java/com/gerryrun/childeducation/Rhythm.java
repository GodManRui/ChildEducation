package com.gerryrun.childeducation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gerryrun.childeducation.util.StatusBarColor;

public class Rhythm extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("节奏");
        StatusBarColor.setWindowsTranslucent(this);

    }
}
