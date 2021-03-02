package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.service.BackendService;

import java.util.concurrent.Callable;

public class RemoveTokenTask implements Callable<Void> {

    private final String accessToken;
    private final String deviceToken;

    public RemoveTokenTask(String accessToken, String deviceToken) {
        super();
        this.accessToken = accessToken;
        this.deviceToken = deviceToken;
    }

    @Override
    public Void call() {
         return BackendService.getInstance().removeToken(accessToken, deviceToken);
    }
}
