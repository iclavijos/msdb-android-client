package com.icesoft.msdb.android;

import android.app.Application;

import com.icesoft.msdb.android.exception.handler.MaintenanceExceptionHandler;

public class MSDBApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new MaintenanceExceptionHandler(this);
    }
}
