package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.UserSubscription;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateUserSubscriptionsTask implements Callable<Void> {

    private final String accessToken;
    private final List<UserSubscription> userSubscriptions;

    @Override
    public Void call() {
         return BackendService.getInstance().updateUserSubscriptions(accessToken, userSubscriptions);
    }
}
