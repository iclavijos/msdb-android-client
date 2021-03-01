package com.icesoft.msdb.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.tasks.UpdateUserSubscriptionsTask;
import com.icesoft.msdb.android.ui.usersubscriptions.UserSubscriptionsViewModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserSubscriptionsActivity extends AppCompatActivity {
    private static final String TAG = "UserSubscriptionActivity";

    private UserSubscriptionsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        viewModel = new ViewModelProvider(this).get(UserSubscriptionsViewModel.class);
        viewModel.setAccessToken(intent.getStringExtra("accessToken"));
        setContentView(R.layout.activity_user_subscriptions);

        findViewById(R.id.saveButton).setOnClickListener((view) -> {
            Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();

            viewModel.getUserSubscriptions().forEach(userSubscription -> {
                if (!userSubscription.isValid()) userSubscription.reset();
            });
            UpdateUserSubscriptionsTask task = new UpdateUserSubscriptionsTask(
                    "Bearer " + intent.getStringExtra("accessToken"),
                    viewModel.getUserSubscriptions()
            );
            Future<Void> opResult = Executors.newFixedThreadPool(1).submit(task);
            try {
                opResult.get();
                opResult.isDone();
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Couldn't update preferences", e);
                Toast.makeText(this, getString(R.string.savingError, e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }
}