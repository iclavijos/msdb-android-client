package com.icesoft.msdb.android.ui.eventdetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.EventSession;

import java.util.List;

public class EventDetailsViewModel extends ViewModel {

    private final MutableLiveData<List<EventSession>> sessions = new MutableLiveData<>();
    private EventEdition eventEdition;
    private String accessToken;

    public void setEventSessions(List<EventSession> sessions) {
        this.sessions.setValue(sessions);
    }

    public LiveData<List<EventSession>> getEventSessions() {
        return sessions;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public EventEdition getEventEdition() {
        return eventEdition;
    }

    public void setEventEdition(EventEdition eventEdition) {
        this.eventEdition = eventEdition;
    }
}
