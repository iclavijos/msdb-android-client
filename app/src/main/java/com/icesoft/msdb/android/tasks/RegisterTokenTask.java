package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.service.BackendService;

import java.util.concurrent.Callable;

public class RegisterTokenTask implements Callable<Void> {

    private final String accessToken;
    private final String deviceToken;

    public RegisterTokenTask(String accessToken, String deviceToken) {
        super();
        this.accessToken = accessToken;
        this.deviceToken = deviceToken;
    }

    @Override
    public Void call() {
         return new BackendService().registerToken(accessToken, deviceToken);
    }
}
