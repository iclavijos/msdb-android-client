package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.UserSubscription;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;

public class UpdateUserSubscriptionsTask implements Callable<Void> {

    private final String accessToken;
    private final List<UserSubscription> userSubscriptions;

    public UpdateUserSubscriptionsTask(String accessToken, List<UserSubscription> userSubscriptions) {
        this.accessToken = accessToken;
        this.userSubscriptions = userSubscriptions;
    }

    @Override
    public Void call() {
         return BackendService.getInstance().updateUserSubscriptions(accessToken, userSubscriptions);
    }
}
