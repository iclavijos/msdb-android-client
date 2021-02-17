package com.icesoft.msdb.android.ui.usersubscriptions;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.UserSubscription;
import com.icesoft.msdb.android.tasks.GetActiveSeriesTask;
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

public class UserSubscriptionsViewModel extends ViewModel {
    private static final String TAG = "UserSubscriptionsViewModel";

    private List<UserSubscription> fullList;
    private MutableLiveData<List<UserSubscription>> userSubscriptionsMutableData;
    private String accessToken;

    public UserSubscriptionsViewModel() {
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
                    .peek(userSubscription -> userSubscription.getSeriesEditionName().toLowerCase())
                .filter(userSubscription -> userSubscription.getSeriesEditionName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList()));
    }

    private List<UserSubscription> fetchUserSubscriptions() {
        CountDownLatch doneSignal = new CountDownLatch(2);
        GetActiveSeriesTask getSeriesTask = new GetActiveSeriesTask(doneSignal);
        GetUserSubscriptionsTask getUserSubscriptionsTask = new GetUserSubscriptionsTask(accessToken, doneSignal);

        List<UserSubscription> result = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<List<ActiveSeries>> opSeries = executor.submit(getSeriesTask);
        Future<List<UserSubscription>> opSubscriptions = executor.submit(getUserSubscriptionsTask);

        try {
            doneSignal.await();

            // Build data structure
            List<UserSubscription> currentSubs = opSubscriptions.get();
            opSeries.get().forEach(currentSeries -> {
                result.add(
                    currentSubs.stream()
                        .filter(currentSub -> currentSub.getSeriesEditionId().equals(currentSeries.getId()))
                        .findFirst()
                            .map(userSubscription -> {
                                userSubscription.setSeriesEditionName(currentSeries.getEditionName());
                                userSubscription.setSeriesLogo(currentSeries.getSeriesLogo());
                                return userSubscription;
                            })
                        .orElse(new UserSubscription(currentSeries.getId(), currentSeries.getEditionName())));
            });
            return result;
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Couldn't retrieve user subscriptions on time", e);
            return Collections.emptyList();
        }
    }
}