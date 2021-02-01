package com.icesoft.msdb.client.ui.upcomingsessions;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icesoft.msdb.client.R;

/**
 * A fragment representing a list of Items.
 */
public class UpcomingSessionsFragment extends Fragment {

    private UpcomingSessionsViewModel upcomingSessionsViewModel;

    public UpcomingSessionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_sessions_list, container, false);

        upcomingSessionsViewModel =
                new ViewModelProvider(this).get(UpcomingSessionsViewModel.class);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            upcomingSessionsViewModel.getUpcomingSessions().observe(getViewLifecycleOwner(), upcomingSessions -> {
                recyclerView.setAdapter(new UpcomingSessionsRecyclerViewAdapter(getContext()));
            });
        }
        return view;
    }
}