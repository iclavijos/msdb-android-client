package com.icesoft.msdb.client.ui.upcomingsessions;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.icesoft.msdb.client.model.UpcomingSession;
import com.icesoft.msdb.client.tasks.UpcomingSessionsTask;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UpcomingSessionsViewModel extends ViewModel {

    private MutableLiveData<List<UpcomingSession>> upcomingSessionsMutableData;

    private Map<LocalDate, List<UpcomingSession>> sessionsPerDay = new HashMap<>();

    public UpcomingSessionsViewModel() {
        upcomingSessionsMutableData = new MutableLiveData<>();
        if (upcomingSessionsMutableData == null) {
            upcomingSessionsMutableData = new MutableLiveData<>();
        }
        upcomingSessionsMutableData.setValue(fetchUpcomingSessions());
    }

    public MutableLiveData<List<UpcomingSession>> getUpcomingSessions() {
        return upcomingSessionsMutableData;
    }

    public List<LocalDate> getDays() {
        return sessionsPerDay.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<UpcomingSession> getSessionsDay(LocalDate date) {
        return sessionsPerDay.get(date);
    }

    private List<UpcomingSession> fetchUpcomingSessions() {
        UpcomingSessionsTask task = new UpcomingSessionsTask();
        Future<List<UpcomingSession>> opResult = Executors.newFixedThreadPool(1).submit(task);
        try {
            List<UpcomingSession> sessions = opResult.get();
            sessions.forEach(upcomingSession -> {
                LocalDate startDate = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(upcomingSession.getSessionStartTime()), ZoneId.systemDefault()).toLocalDate();
                List<UpcomingSession> sessionsDay;
                if (!sessionsPerDay.containsKey(startDate)) {
                    sessionsPerDay.put(startDate, new ArrayList<>());
                }
                sessionsPerDay.get(startDate).add(upcomingSession);
            });
            return sessions;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
