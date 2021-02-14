package com.icesoft.msdb.android.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.ui.upcomingsessions.UpcomingSessionsRecyclerViewAdapter;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.upcomingSessionsRefresh);

        refreshLayout.setOnRefreshListener(() -> {
            RecyclerView recyclerView =
                    (RecyclerView)view.findViewById(R.id.upcomingSessionsReciclerView);
            ((UpcomingSessionsRecyclerViewAdapter)recyclerView.getAdapter()).refreshData();
            refreshLayout.setRefreshing(false);
        });

        return view;
    }
}