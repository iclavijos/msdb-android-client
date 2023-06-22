package com.icesoft.msdb.android.ui.upcomingsessions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.icesoft.msdb.android.R;

/**
 * A fragment representing a list of Items.
 */
public class UpcomingSessionsFragment extends Fragment {

    private SecureCredentialsManager credentialsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_sessions_list, container, false);

        Auth0 auth0 = new Auth0(requireContext());
        credentialsManager = new SecureCredentialsManager(
                requireContext(),
                new AuthenticationAPIClient(auth0),
                new SharedPreferencesStorage(requireContext()));

        UpcomingSessionsViewModel upcomingSessionsViewModel = new ViewModelProvider(this).get(UpcomingSessionsViewModel.class);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            upcomingSessionsViewModel.getUpcomingSessions().observe(getViewLifecycleOwner(), upcomingSessions ->
                    recyclerView.setAdapter(new UpcomingSessionsRecyclerViewAdapter(getContext(), credentialsManager)));
        }
        return view;
    }
}