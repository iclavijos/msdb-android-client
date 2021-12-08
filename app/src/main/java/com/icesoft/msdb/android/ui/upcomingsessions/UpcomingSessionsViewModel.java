package com.icesoft.msdb.android.ui.upcomingsessions;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.tasks.UpcomingSessionsTask;

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class UpcomingSessionsViewModel extends ViewModel {

    private static final String TAG = "UpcomingSessionsViewModel";

    private MutableLiveData<List<UpcomingSession>> upcomingSessionsMutableData;

    private Map<LocalDate, List<UpcomingSession>> sessionsPerDay = new HashMap<>();

    public UpcomingSessionsViewModel() {
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

    public void clear() {
        sessionsPerDay.clear();
        upcomingSessionsMutableData.getValue().clear();
    }

    public void refreshUpcomingSessions() {
        upcomingSessionsMutableData.getValue().clear();
        upcomingSessionsMutableData.setValue(fetchUpcomingSessions());
    }

    private List<UpcomingSession> fetchUpcomingSessions() {
        UpcomingSessionsTask task = new UpcomingSessionsTask();
        Future<List<UpcomingSession>> opResult = Executors.newFixedThreadPool(1).submit(task);
        try {
            List<UpcomingSession> sessions = opResult.get(10000, TimeUnit.MILLISECONDS);
            if (sessions == null) {
                return Collections.emptyList();
            }
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
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            Log.e(TAG, "Couldn't retrieve upcoming sessions", e);
            return Collections.emptyList();
        }
    }
}
