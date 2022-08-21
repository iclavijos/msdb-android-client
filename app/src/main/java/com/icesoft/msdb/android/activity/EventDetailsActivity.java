package com.icesoft.msdb.android.activity;

import android.content.Intent;
import android.os.Bundle;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.EventSession;
import com.icesoft.msdb.android.tasks.GetEventDetailsTask;
import com.icesoft.msdb.android.tasks.GetEventSessionsTask;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsInfoFragment;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsPagerAdapter;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsParticipantsFragment;
import com.icesoft.msdb.android.ui.eventdetails.EventDetailsViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";

    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;

    private EventDetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        EventDetailsPagerAdapter eventDetailsPagerAdapter = new EventDetailsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.event_details_pager);
        viewPager.setAdapter(eventDetailsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);

        auth0 = new Auth0(this);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

        TextView eventEditionNameTextView = findViewById(R.id.eventDetailsEventNameTextView);
        eventEditionNameTextView.setText(getIntent().getStringExtra("eventName"));

        EventEdition eventDetails;
        if (getIntent().getStringExtra("accessToken") != null) {
            viewModel.setAccessToken(getIntent().getStringExtra("accessToken"));
            eventDetails = getEventEdition(
                    getIntent().getStringExtra("accessToken"),
                    getIntent().getLongExtra("eventEditionId", 0L));
        } else {
            final EventEdition[] tmp = new EventEdition[1];
            credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
                @Override
                public void onSuccess(@Nullable Credentials payload) {
                    viewModel.setAccessToken(payload.getIdToken());
                    tmp[0] = getEventEdition(payload.getIdToken(), getIntent().getLongExtra("eventEditionId", 0L));
                }

                @Override
                public void onFailure(@NonNull CredentialsManagerException error) {

                }
            });
            eventDetails = tmp[0];
        }

        if (eventDetails != null) {
            EventDetailsInfoFragment eventInfoFragment = new EventDetailsInfoFragment();
            eventDetailsPagerAdapter.addInfoFragment(getString(R.string.eventDetailsInfoTab), eventInfoFragment);

            EventDetailsParticipantsFragment participantsFragment = new EventDetailsParticipantsFragment();
            eventDetailsPagerAdapter.addParticipantsFragment(getString(R.string.eventDetailsParticipantsTab), participantsFragment);
            eventDetailsPagerAdapter.notifyDataSetChanged();

            viewModel.setEventEdition(eventDetails);
            viewModel.setEventSessions(getEventSessions(
                    viewModel.getAccessToken(),
                    eventDetails.getId()));
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private EventEdition getEventEdition(String accessToken, Long eventEditionId) {
        EventEdition eventDetails = null;
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
        List<EventSession> eventSessions = null;
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