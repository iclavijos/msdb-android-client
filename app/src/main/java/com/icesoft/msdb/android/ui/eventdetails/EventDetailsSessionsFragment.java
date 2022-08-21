package com.icesoft.msdb.android.ui.eventdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventEdition;

public class EventDetailsSessionsFragment extends Fragment {

    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;

    private EventEdition eventDetails;
    private EventDetailsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);
        eventDetails = viewModel.getEventEdition();

        View view = inflater.inflate(R.layout.fragment_event_sessions_list, container, false);

        auth0 = new Auth0(getContext());
        credentialsManager = new SecureCredentialsManager(getContext(), new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(getContext()));

        RecyclerView recyclerView = view.findViewById(R.id.eventSessionsRecyclerView);
        viewModel.getEventSessions().observe(getViewLifecycleOwner(), eventSessions -> {
            recyclerView.setAdapter(new EventSessionRecyclerViewAdapter(eventDetails, eventSessions));
        });

        return view;
    }
}