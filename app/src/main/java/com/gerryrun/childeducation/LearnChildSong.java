package com.gerryrun.childeducation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gerryrun.childeducation.util.StatusBarColor;

public class LearnChildSong extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_child_song);
        findViewById(R.id.im_go_home).setOnClickListener(v -> {
            startActivity(new Intent(LearnChildSong.this, LearnChildSong.class));
            finish();
        });
        findViewById(R.id.xiaoxingxing).setOnClickListener(v -> {
            startActivity(new Intent(LearnChildSong.this, StartLearnSong2.class));
            finish();
        });
    }
}
