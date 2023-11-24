package com.icesoft.msdb.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.icesoft.msdb.android.exception.handler.MaintenanceExceptionHandler;

public class MSDBApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = this.getApplicationContext()
                .getSharedPreferences("SeriesEditionDetailPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("series_edition_spinner_item").apply();
        new MaintenanceExceptionHandler(this);
    }

}
