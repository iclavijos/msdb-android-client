package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;

public class UpcomingSessionsTask implements Callable<List<UpcomingSession>> {

    @Override
    public List<UpcomingSession> call() {
        return new BackendService().getUpcomingSessions();
    }
}
