package com.gerryrun.childeducation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class Guess extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_guess_type_select);
        setTitle("竞猜");
        initView();
    }

    private void initView() {
        findViewById(R.id.im_guess_go_home).setOnClickListener(v -> finish());
        findViewById(R.id.im_guess_type_life).setOnClickListener(v -> startNextActivity(1));
        findViewById(R.id.im_guess_type_musical).setOnClickListener(v -> startNextActivity(2));
    }

    private void startNextActivity(int i) {
        Intent intent = new Intent(this, GuessDifferentiate.class);
        intent.putExtra("guess_type", i);
        startActivity(intent);
    }
}
