package com.gerryrun.childeducation;

import android.app.Application;

import tech.linjiang.pandora.Pandora;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Pandora.init(this).open();
    }
}
