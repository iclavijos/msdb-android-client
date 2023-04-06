package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.Series;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class GetLatestVersionTask implements Callable<String> {

    private final CountDownLatch doneSignal;

    public GetLatestVersionTask(CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
    }

    @Override
    public String call() {
        String result = BackendService.getInstance().getLatestVersion();
        doneSignal.countDown();
        return result;
    }
}
