package com.icesoft.msdb.android.ui.upcomingsessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.icesoft.msdb.android.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.icesoft.msdb.android.model.UpcomingSession}.
 */
public class UpcomingSessionsRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingSessionsRecyclerViewAdapter.ViewHolder> {

    private final UpcomingSessionsViewModel upcomingSessionsViewModel;
    private final SharedPreferences sharedPreferences;

    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public UpcomingSessionsRecyclerViewAdapter(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        upcomingSessionsViewModel =
                new ViewModelProvider((FragmentActivity)context).get(UpcomingSessionsViewModel.class);

        upcomingSessionsViewModel.refreshUpcomingSessions();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_upcoming_sessions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocalDate startDate = upcomingSessionsViewModel.getDays().get(position);
        String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(startDate);
        holder.mIdView.setText(formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1));

        UpcomingSessionsDayRecyclerViewAdapter sessionsDayViewAdapter =
                new UpcomingSessionsDayRecyclerViewAdapter(holder.mView.getContext(), upcomingSessionsViewModel.getSessionsDay(startDate));
        holder.mUpcomingSessionsDayView.setAdapter(sessionsDayViewAdapter);
        holder.mUpcomingSessionsDayView.setRecycledViewPool(viewPool);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        holder.mUpcomingSessionsDayView.getContext(),
                        LinearLayoutManager.VERTICAL,
                        false);
        layoutManager.setInitialPrefetchItemCount(
                upcomingSessionsViewModel.getSessionsDay(startDate).size());
        holder.mUpcomingSessionsDayView.setLayoutManager(layoutManager);
        sessionsDayViewAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return upcomingSessionsViewModel.getDays().size();
    }

    public void refreshData() {
        int numItems = upcomingSessionsViewModel.getDays().size();
        upcomingSessionsViewModel.clear();
        notifyItemRangeRemoved(0, numItems);
        upcomingSessionsViewModel.refreshUpcomingSessions();
        notifyDataSetChanged();
    }

    public void filterSeries() {
        List<String> enabledSeries = sharedPreferences.getAll().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("series-") && ((Boolean) entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        upcomingSessionsViewModel.setEnabledSeries(enabledSeries);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final RecyclerView mUpcomingSessionsDayView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.day);
            mUpcomingSessionsDayView = view.findViewById(R.id.upcomingSessionsDayReciclerView);
        }

    }
}