package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.Series;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class GetSeriesTask implements Callable<List<Series>> {

    private final CountDownLatch doneSignal;

    public GetSeriesTask(CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
    }

    @Override
    public List<Series> call() {
        List<Series> result = BackendService.getInstance().getSeries();
        doneSignal.countDown();
        return result;
    }
}
