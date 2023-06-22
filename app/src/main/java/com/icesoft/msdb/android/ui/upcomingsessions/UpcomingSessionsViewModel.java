package com.icesoft.msdb.android.ui.upcomingsessions;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class UpcomingSessionsViewModel extends ViewModel {

    private static final String TAG = "UpcomingSessionsViewModel";

    private final LoadingCache<String, List<UpcomingSession>> cache;

    private List<String> enabledSeriesIds = Collections.emptyList();
    private List<UpcomingSession> upcomingSessions;
    private MutableLiveData<List<UpcomingSession>> upcomingSessionsMutableData;

    private final Map<LocalDate, List<UpcomingSession>> sessionsPerDay = new HashMap<>();

    public UpcomingSessionsViewModel() {
        Log.d(TAG, "Instantiating fragment...");
        cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(key -> {
                        Log.d(TAG, "Launching task to retrieve upcoming sessions...");
                        UpcomingSessionsTask task = new UpcomingSessionsTask();
                        Future<List<UpcomingSession>> opResult = Executors.newFixedThreadPool(1).submit(task);
                        try {
                            List<UpcomingSession> sessions = opResult.get(10000, TimeUnit.MILLISECONDS);
                            return Optional.ofNullable(sessions).orElse(Collections.emptyList());
                        } catch (TimeoutException | ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Couldn't retrieve upcoming sessions", e);
                            if (e.getCause() instanceof MSDBException) {
                                throw (MSDBException) e.getCause();
                            }
                            return Collections.emptyList();
                        }
                    });

        if (upcomingSessionsMutableData == null) {
            upcomingSessionsMutableData = new MutableLiveData<>();
        }

        upcomingSessionsMutableData.setValue(Collections.emptyList());
    }

    public void setEnabledSeries(List<String> enabledSeries) {
        this.enabledSeriesIds = enabledSeries;

        upcomingSessionsMutableData.getValue().clear();
        upcomingSessionsMutableData.setValue(filterUpcomingSessions());
    }

    public MutableLiveData<List<UpcomingSession>> getUpcomingSessions() {
        Log.d(TAG, "Getting mutable data object...");
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
        Log.d(TAG, "Refreshing upcoming sessions...");
        fetchUpcomingSessions();
        List<UpcomingSession> filteredSessions = filterUpcomingSessions();
        upcomingSessionsMutableData.getValue().clear();
        upcomingSessionsMutableData.setValue(filteredSessions);
    }

    private List<UpcomingSession> filterUpcomingSessions() {
        Log.d(TAG, "Filtering upcoming sessions...");
        sessionsPerDay.clear();

        return upcomingSessions
            .stream()
            .filter(upcomingSession ->
                    Objects.requireNonNull(upcomingSession.getSeriesIds()).stream()
                            .anyMatch(id -> enabledSeriesIds.contains("series-" + id) || enabledSeriesIds.isEmpty()))
            .peek(upcomingSession -> {
                LocalDate startDate = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(upcomingSession.getSessionStartTime()), ZoneId.systemDefault()).toLocalDate();

                if (!sessionsPerDay.containsKey(startDate)) {
                    sessionsPerDay.put(startDate, new ArrayList<>());
                }
                Objects.requireNonNull(sessionsPerDay.get(startDate)).add(upcomingSession);
            }).collect(Collectors.toList());
    }

    private void fetchUpcomingSessions() {
        Log.d(TAG, "Fetching upcoming sessions...");
        upcomingSessions = cache.get("upcomingSessions");
    }
}
