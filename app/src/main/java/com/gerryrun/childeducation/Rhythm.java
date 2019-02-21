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
        setContentView(R.layout.rhythm);
    }
}
