package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class GetActiveSeriesTask implements Callable<List<ActiveSeries>> {

    private final CountDownLatch doneSignal;

    public GetActiveSeriesTask(CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
    }

    @Override
    public List<ActiveSeries> call() {
        List<ActiveSeries> result = BackendService.getInstance().getActiveSeries();
        doneSignal.countDown();
        return result;
    }
}
