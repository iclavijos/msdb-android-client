package com.icesoft.msdb.android.exception.handler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.InflateException;

import com.icesoft.msdb.android.activity.MaintenanceActivity;
import com.icesoft.msdb.android.exception.MSDBMaintenanceException;

import java.util.zip.Inflater;

public class MaintenanceExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = MaintenanceExceptionHandler.class.getSimpleName();
    public static final String EXTRA_MY_EXCEPTION_HANDLER = "EXTRA_MY_EXCEPTION_HANDLER";

    private final Context context;
    private final Thread.UncaughtExceptionHandler rootHandler;

    public MaintenanceExceptionHandler(Context context) {
        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        try {
            Log.d(TAG, "called for " + ex.getClass());
            if (getRootCause(ex) instanceof MSDBMaintenanceException) {
                Intent registerActivity = new Intent(context, MaintenanceActivity.class);
                registerActivity.putExtra(EXTRA_MY_EXCEPTION_HANDLER, MaintenanceExceptionHandler.class.getName());
                registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                registerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(registerActivity);

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            } else {
                rootHandler.uncaughtException(thread, ex);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception handler failed!", e);
        }
    }

    private RuntimeException getRootCause(Throwable t) {
        if (t.getCause() != null) {
            if (t.getCause() instanceof InflateException) {
                return getRootCause(t.getCause());
            }
            return (RuntimeException) t.getCause();
        }
        return (RuntimeException) t;
    }
}
