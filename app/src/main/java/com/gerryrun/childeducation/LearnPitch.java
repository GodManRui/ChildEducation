package com.gerryrun.childeducation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.gerryrun.childeducation.customview.PianoPitch;
import com.gerryrun.childeducation.util.StatusBarColor;

public class LearnPitch extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarColor.setWindowsTranslucent(this);
        setContentView(R.layout.learn_pitch);
//        CreateView();
//        setTitle("学音高");
    }

    private void CreateView() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(new PianoPitch(this));
        setContentView(linearLayout);
    }
}
