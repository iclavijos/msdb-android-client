package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.UserSubscription;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;

public class GetUserSubscriptionsTask implements Callable<List<UserSubscription>> {

    private final String accessToken;

    public GetUserSubscriptionsTask(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public List<UserSubscription> call() {
        return BackendService.getInstance().getUserSubscriptions(accessToken);
    }
}
