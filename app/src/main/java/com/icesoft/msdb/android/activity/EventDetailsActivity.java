package com.icesoft.msdb.android.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.auth0.android.result.Credentials;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.EventSession;
import com.icesoft.msdb.android.service.MSDBService;
import com.icesoft.msdb.android.tasks.GetEventDetailsTask;
import com.icesoft.msdb.android.tasks.GetEventSessionsTask;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsInfoFragment;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsPagerAdapter;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsParticipantsFragment;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsViewModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";

    private MSDBService msdbService;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MSDBService.LocalBinder binder = (MSDBService.LocalBinder) service;
            msdbService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MSDBService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = findViewById(R.id.eventDetailsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EventDetailsPagerAdapter eventDetailsPagerAdapter = new EventDetailsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.event_details_pager);
        viewPager.setAdapter(eventDetailsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Log.d(TAG, "onCreate: Instantiating view model");
        EventDetailsViewModel viewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);

        TextView eventEditionNameTextView = findViewById(R.id.eventDetailsEventNameTextView);
        eventEditionNameTextView.setText(getIntent().getStringExtra("eventName"));

        Log.d(TAG, "onCreate: Preparing eventEdition");
        final AtomicReference<EventEdition> atomicEventDetails = new AtomicReference<>();

        if (getIntent().getStringExtra("accessToken") != null) {
            viewModel.setAccessToken(getIntent().getStringExtra("accessToken"));
            atomicEventDetails.set(
                    getEventEdition(
                    getIntent().getStringExtra("accessToken"),
                    getIntent().getLongExtra("eventEditionId", 0L)));


        } else {
            Log.d(TAG, "onMessageReceived: countdown created");
            final CountDownLatch awaitCredentialsSignal = new CountDownLatch(1);

            Credentials credentials;
            try {
                credentials = BuildersKt.runBlocking(
                        EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> msdbService.getCredentials(continuation)
                );
            } catch (InterruptedException e) {
                Log.e(TAG, "Couldn't retrieve credentials");
                credentials = null;
            }

            if (credentials != null) {
                atomicEventDetails.set(getEventEdition(
                        credentials.getAccessToken(),
                        getIntent().getLongExtra("eventEditionId", 0L)));
                viewModel.setAccessToken(credentials.getAccessToken());
                awaitCredentialsSignal.countDown();
                Log.d(TAG, "onSuccess: countdown decreased");
            } else {
                awaitCredentialsSignal.countDown();
                Log.d(TAG, "onSuccess: countdown decreased onFailure");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            try {
                Log.d(TAG, "onMessageReceived: awaiting...");
                awaitCredentialsSignal.await();
                Log.d(TAG, "onMessageReceived: proceeding...");
            } catch (InterruptedException e) {
                Log.e(TAG, "onMessageReceived: ", e);
            }
        }

        EventDetailsInfoFragment eventInfoFragment = new EventDetailsInfoFragment();
        eventDetailsPagerAdapter.addInfoFragment(getString(R.string.eventDetailsInfoTab), eventInfoFragment);

        EventDetailsParticipantsFragment participantsFragment = new EventDetailsParticipantsFragment();
        eventDetailsPagerAdapter.addParticipantsFragment(getString(R.string.eventDetailsParticipantsTab), participantsFragment);
        eventDetailsPagerAdapter.notifyDataSetChanged();

        EventEdition eventEdition = atomicEventDetails.get();
        viewModel.setEventEdition(eventEdition);
        viewModel.setEventSessions(getEventSessions(
                viewModel.getAccessToken(),
                eventEdition.getId()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }

    private EventEdition getEventEdition(String accessToken, Long eventEditionId) {
        EventEdition eventDetails;
        GetEventDetailsTask eventDetailsTask = new GetEventDetailsTask(accessToken, eventEditionId);
        Future<EventEdition> opResult = Executors.newFixedThreadPool(1).submit(eventDetailsTask);
        try {
            eventDetails = opResult.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Couldn't retrieve event details", e);
            Toast.makeText(getApplicationContext(), "Couldn't retrieve event details", Toast.LENGTH_SHORT).show();
            return null;
        }

        return eventDetails;
    }

    private List<EventSession> getEventSessions(String accessToken, Long eventEditionId) {
        List<EventSession> eventSessions;
        GetEventSessionsTask task = new GetEventSessionsTask(accessToken, eventEditionId);
        Future<List<EventSession>> opResult = Executors.newFixedThreadPool(1).submit(task);
        try {
            eventSessions = opResult.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Couldn't retrieve event sessions", e);
            Toast.makeText(getApplicationContext(), "Couldn't retrieve event sessions", Toast.LENGTH_SHORT).show();
            return null;
        }

        return eventSessions;
    }
}