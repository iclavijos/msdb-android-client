package com.icesoft.msdb.android.ui.upcomingsessions;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.icesoft.msdb.android.exception.MSDBException;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class UpcomingSessionsViewModel extends ViewModel {

    private static final String TAG = "UpcomingSessionsViewModel";

    private List<String> enabledSeriesIds = Collections.EMPTY_LIST;
    private List<UpcomingSession> upcomingSessions;
    private MutableLiveData<List<UpcomingSession>> upcomingSessionsMutableData;

    private Map<LocalDate, List<UpcomingSession>> sessionsPerDay = new HashMap<>();

    public UpcomingSessionsViewModel() {
        if (upcomingSessionsMutableData == null) {
            upcomingSessionsMutableData = new MutableLiveData<>();
        }
        fetchUpcomingSessions();
        upcomingSessionsMutableData.setValue(Collections.EMPTY_LIST);
    }

    public void setEnabledSeries(List<String> enabledSeries) {
        this.enabledSeriesIds = enabledSeries;

        upcomingSessionsMutableData.getValue().clear();
        upcomingSessionsMutableData.setValue(filterUpcomingSessions());
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
        fetchUpcomingSessions();
        List<UpcomingSession> filteredSessions = filterUpcomingSessions();
        upcomingSessionsMutableData.getValue().clear();
        upcomingSessionsMutableData.setValue(filteredSessions);
    }

    private List<UpcomingSession> filterUpcomingSessions() {
        List<UpcomingSession> filteredSessions = upcomingSessions
            .stream()
            .filter(upcomingSession ->
                    upcomingSession.getSeriesIds().stream()
                            .anyMatch(id -> enabledSeriesIds.contains("series-" + id) || enabledSeriesIds.isEmpty()))
            .peek(upcomingSession -> {
                LocalDate startDate = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(upcomingSession.getSessionStartTime()), ZoneId.systemDefault()).toLocalDate();

                if (!sessionsPerDay.containsKey(startDate)) {
                    sessionsPerDay.put(startDate, new ArrayList<>());
                }
                sessionsPerDay.get(startDate).add(upcomingSession);
            }).collect(Collectors.toList());

        return filteredSessions;
    }

    private void fetchUpcomingSessions() {
        UpcomingSessionsTask task = new UpcomingSessionsTask();
        Future<List<UpcomingSession>> opResult = Executors.newFixedThreadPool(1).submit(task);
        try {
            List<UpcomingSession> sessions = opResult.get(10000, TimeUnit.MILLISECONDS);
            upcomingSessions = Optional.ofNullable(sessions).orElse(Collections.emptyList());

        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            Log.e(TAG, "Couldn't retrieve upcoming sessions", e);
            upcomingSessions = Collections.emptyList();
            if (e.getCause() instanceof MSDBException) {
                throw (MSDBException)e.getCause();
            }
        }
    }
}
