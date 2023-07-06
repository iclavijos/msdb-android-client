package com.icesoft.msdb.android.tasks;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.icesoft.msdb.android.model.Series;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GetSeriesTask implements Callable<List<Series>> {

    private final CountDownLatch doneSignal;

    private final Cache<String, List<Series>> cache;

    public GetSeriesTask(CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
        cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public List<Series> call() {
        List<Series> result = cache.get("series", k-> BackendService.getInstance().getSeries());
        doneSignal.countDown();
        return result;
    }
}
