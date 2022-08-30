package com.icesoft.msdb.android.ui.usersubscriptions;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.icesoft.msdb.android.exception.MSDBException;
import com.icesoft.msdb.android.model.Series;
import com.icesoft.msdb.android.model.UserSubscription;
import com.icesoft.msdb.android.tasks.GetSeriesTask;
import com.icesoft.msdb.android.tasks.GetUserSubscriptionsTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UserSubscriptionsViewModel extends AndroidViewModel {
    private static final String TAG = "UserSubscriptionsViewModel";

    private List<UserSubscription> fullList;
    private MutableLiveData<List<UserSubscription>> userSubscriptionsMutableData;
    private String accessToken;

    public UserSubscriptionsViewModel(Application application) {
        super(application);
        userSubscriptionsMutableData = new MutableLiveData<>();
    }

    public MutableLiveData<List<UserSubscription>> getMutableUserSubscriptions() {
        if (userSubscriptionsMutableData == null) {
            userSubscriptionsMutableData = new MutableLiveData<>();
        }
        fullList = fetchUserSubscriptions();
        userSubscriptionsMutableData.setValue(new ArrayList<>(fullList));

        return userSubscriptionsMutableData;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<UserSubscription> getUserSubscriptions() {
        return Optional.ofNullable(userSubscriptionsMutableData.getValue()).orElse(Collections.EMPTY_LIST);
    }

    public void filterUserSubscriptions(String filter) {
        userSubscriptionsMutableData.getValue().clear();
        if (TextUtils.isEmpty(filter)) {
            userSubscriptionsMutableData.setValue(new ArrayList<>(fullList));
            return;
        }
        userSubscriptionsMutableData.setValue(
            fullList.stream()
                .filter(userSubscription -> userSubscription.getSeriesName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList()));
    }

    private List<UserSubscription> fetchUserSubscriptions() {
        CountDownLatch doneSignal = new CountDownLatch(2);
        GetUserSubscriptionsTask getUserSubscriptionsTask = new GetUserSubscriptionsTask(accessToken, doneSignal);

        List<UserSubscription> result = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Series> series = getSeriesList(executor, doneSignal);
        Future<List<UserSubscription>> opSubscriptions = executor.submit(getUserSubscriptionsTask);

        try {
            doneSignal.await();

            // Build data structure
            List<UserSubscription> currentSubs = opSubscriptions.get();
            series.forEach(currentSeries -> {
                result.add(
                        currentSubs.stream()
                                .filter(currentSub -> currentSub.getSeriesId().equals(currentSeries.getId()))
                                .findFirst()
                                .map(userSubscription -> {
                                    userSubscription.setSeriesName(currentSeries.getName());
                                    userSubscription.setSeriesLogo(currentSeries.getLogoUrl());
                                    return userSubscription;
                                })
                                .orElse(new UserSubscription(
                                        currentSeries.getId(),
                                        currentSeries.getName(),
                                        currentSeries.getLogoUrl())));
            });
            return result;
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Couldn't retrieve user subscriptions on time", e);
            return Collections.emptyList();
        }
    }

    private List<Series> getSeriesList(ExecutorService executor, CountDownLatch doneSignal) {
        List<Series> result;

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplication());
        if (sharedPreferences.contains("seriesList")) {
            JsonMapper mapper = new JsonMapper();
            String seriesList = sharedPreferences.getString("seriesList", "[]");
            try {
                result = mapper.readValue(seriesList, new TypeReference<ArrayList<Series>>(){});
            } catch (JsonProcessingException e) {
                Log.e(TAG, "Couldn't deserialize series list " + seriesList);
                throw new MSDBException(e);
            }
            doneSignal.countDown();
        } else {
            GetSeriesTask getSeriesTask = new GetSeriesTask(doneSignal);
            Future<List<Series>> opSeries = executor.submit(getSeriesTask);
            try {
                return Optional.ofNullable(opSeries.get()).orElseGet(() -> Collections.EMPTY_LIST);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Couldn't retrieve series list on time", e);
                return Collections.emptyList();
            }
        }

        return result;
    }
}