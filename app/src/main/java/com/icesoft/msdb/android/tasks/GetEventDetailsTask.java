package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.service.BackendService;

import java.util.concurrent.Callable;

public class GetEventDetailsTask implements Callable<EventEdition> {

    private final Long eventEditionId;
    private final String accessToken;

    public GetEventDetailsTask(String accessToken, Long eventEditionId) {
        this.eventEditionId = eventEditionId;
        this.accessToken = accessToken;
    }

    @Override
    public EventEdition call() {
        return BackendService.getInstance().getEventDetails(accessToken, eventEditionId);
    }
}
