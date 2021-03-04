package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.EventSession;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;

public class GetEventSessionsTask implements Callable<List<EventSession>> {

    private final String accessToken;
    private final Long eventEditionId;

    public GetEventSessionsTask(String accessToken, Long eventEditionId) {
        this.accessToken = accessToken;
        this.eventEditionId = eventEditionId;
    }

    @Override
    public List<EventSession> call() {
        return BackendService.getInstance().getEventSessions(accessToken, eventEditionId);
    }
}
