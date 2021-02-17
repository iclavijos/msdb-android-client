package com.icesoft.msdb.android.tasks;

import com.icesoft.msdb.android.model.ActiveSeries;
import com.icesoft.msdb.android.model.UserSubscription;
import com.icesoft.msdb.android.service.BackendService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class GetUserSubscriptionsTask implements Callable<List<UserSubscription>> {

    private final String accessToken;
    private final CountDownLatch doneSignal;

    public GetUserSubscriptionsTask(String accessToken, CountDownLatch doneSignal) {
        this.accessToken = accessToken;
        this.doneSignal = doneSignal;
    }

    @Override
    public List<UserSubscription> call() {
        List<UserSubscription> result = BackendService.getInstance().getUserSubscriptions(accessToken);
        doneSignal.countDown();
        return result;
    }
}
