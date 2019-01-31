package com.gerryrun.childeducation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gerryrun.childeducation.util.StatusBarColor;

public class Guess extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("竞猜");
        StatusBarColor.setWindowsTranslucent(this);
    }
}
