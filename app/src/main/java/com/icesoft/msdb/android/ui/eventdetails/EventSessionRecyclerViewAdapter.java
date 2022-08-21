package com.icesoft.msdb.android.ui.eventdetails;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.EventSession;
import com.icesoft.msdb.android.model.enums.DurationType;

import java.text.DecimalFormat;
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

    private final EventEdition eventDetails;
    private final List<EventSession> sessions;

    public EventSessionRecyclerViewAdapter(EventEdition eventDetails, List<EventSession> sessions) {
        this.eventDetails = eventDetails;
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
        if (eventDetails.isRaid()) {
            holder.startDayTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(startTime));
        } else {
            holder.startDayTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(startTime));
            holder.startTimeTextView.setText(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime));
        }
        if (eventDetails.isRally()) {
            holder.sessionNameTextView.setText(String.format("%s - %s", session.getShortname(), session.getName()));
        } else {
            holder.sessionNameTextView.setText(session.getName());
        }

        holder.durationTextView.setText(
                calculateDuration(session.getDuration(), session.getDurationType(), session.isAdditionalLap()));

        if (!session.isCancelled()) {
            holder.cancelledCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    private String calculateDuration(Float duration, DurationType durationType, Boolean extraLap) {
        String durationTypeStr;
        String extraLapStr = extraLap ? context.getString(R.string.plusExtraLap) : "";

        switch (durationType) {
            case MINUTES: durationTypeStr = context.getString(R.string.minutes);
                break;
            case HOURS: durationTypeStr = context.getString(R.string.hours);
                break;
            case KMS: durationTypeStr = context.getString(R.string.km);
                break;
            case MILES: durationTypeStr = context.getString(R.string.miles);
                break;
            case LAPS: durationTypeStr = context.getString(R.string.laps);
                break;
            default: durationTypeStr = "unknown";
        }

        String durationStr;
        DecimalFormat df;
        if (eventDetails.isRally()) {
            df = new DecimalFormat("0.00");
        } else {
            df = new DecimalFormat("#");
        }
        durationStr = df.format(duration);
        return String.join(" ", Arrays.asList(durationStr, durationTypeStr, extraLapStr));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView startDayTextView;
        public final TextView startTimeTextView;
        public final TextView sessionNameTextView;
        public final TextView durationTextView;
        public final CardView cancelledCardView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            startDayTextView = mView.findViewById(R.id.sessionStartDayTextView);
            startTimeTextView = mView.findViewById(R.id.sessionStartTimeTextView);
            sessionNameTextView = mView.findViewById(R.id.sessionNameTextView);
            durationTextView = mView.findViewById(R.id.sessionDurationTextView);
            cancelledCardView = mView.findViewById(R.id.sessionCancelledCardView);
        }
    }
}