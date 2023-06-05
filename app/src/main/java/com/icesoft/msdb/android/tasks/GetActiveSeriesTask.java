package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.SeriesEdition;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class GetActiveSeriesTask implements Callable<List<SeriesEdition>> {

    private final CountDownLatch doneSignal;

    public GetActiveSeriesTask(CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
    }

    @Override
    public List<SeriesEdition> call() {
        List<SeriesEdition> result = BackendService.getInstance().getActiveSeriesEditions();
        doneSignal.countDown();
        return result;
    }
}
