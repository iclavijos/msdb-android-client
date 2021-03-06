package com.icesoft.msdb.android.ui.eventdetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventSession;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;

public class EventSessionRecyclerViewAdapter extends RecyclerView.Adapter<EventSessionRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "EventSessionRecyclerViewAdapter";
    private Context context;

    private final List<EventSession> sessions;

    public EventSessionRecyclerViewAdapter(List<EventSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public EventSessionRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event_sessions, parent, false);
        return new EventSessionRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventSessionRecyclerViewAdapter.ViewHolder holder, int position) {
        EventSession session = sessions.get(position);
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(session.getStartTime()),
                ZoneId.systemDefault()
        );
        holder.startTimeTextView.setText(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(startTime));
        holder.sessionNameTextView.setText(session.getName());
        holder.durationTextView.setText(
                calculateDuration(session.getDuration(), session.getDurationType(), session.isAdditionalLap()));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    private String calculateDuration(Integer duration, Integer durationType, Boolean extraLap) {
        String durationTypeStr;
        String extraLapStr = extraLap ? context.getString(R.string.plusExtraLap) : "";

        switch (durationType) {
            case 1: durationTypeStr = context.getString(R.string.minutes);
                break;
            case 2: durationTypeStr = context.getString(R.string.hours);
                break;
            case 3: durationTypeStr = context.getString(R.string.km);
                break;
            case 4: durationTypeStr = context.getString(R.string.miles);
                break;
            case 5: durationTypeStr = context.getString(R.string.laps);
                break;
            default: durationTypeStr = "unknown";
        }

        return String.join(" ", Arrays.asList(duration.toString(), durationTypeStr, extraLapStr));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView startTimeTextView;
        public final TextView sessionNameTextView;
        public final TextView durationTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            startTimeTextView = mView.findViewById(R.id.sessionStartTimeTextView);
            sessionNameTextView = mView.findViewById(R.id.sessionNameTextView);
            durationTextView = mView.findViewById(R.id.sessionDurationTextView);
        }
    }
}