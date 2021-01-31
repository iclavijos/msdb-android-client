package com.icesoft.msdb.client.tasks;

import android.os.AsyncTask;

import com.icesoft.msdb.client.model.UpcomingSession;
import com.icesoft.msdb.client.service.MSDBService;

import java.util.List;
import java.util.concurrent.Callable;

public class UpcomingSessionsTask implements Callable<List<UpcomingSession>> {

    @Override
    public List<UpcomingSession> call() {
        return new MSDBService().getUpcomingSessions();
    }
}
