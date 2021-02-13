package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;

public class GetActiveSeriesTask implements Callable<List<ActiveSeries>> {

    @Override
    public List<ActiveSeries> call() {
        return BackendService.getInstance().getActiveSeries();
    }
}
