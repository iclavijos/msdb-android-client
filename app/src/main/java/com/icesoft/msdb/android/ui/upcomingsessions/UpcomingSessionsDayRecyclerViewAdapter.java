package com.icesoft.msdb.android.ui.upcomingsessions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.UpcomingSession;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class UpcomingSessionsDayRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingSessionsDayRecyclerViewAdapter.ViewHolder> {

    private final List<UpcomingSession> upcomingSessions;

    public UpcomingSessionsDayRecyclerViewAdapter(List<UpcomingSession> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    @NonNull
    @Override
    public UpcomingSessionsDayRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_sessions_day_layout, parent, false);
        return new UpcomingSessionsDayRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingSessionsDayRecyclerViewAdapter.ViewHolder holder, int position) {
        UpcomingSession upcomingSession = upcomingSessions.get(position);
        holder.mViewSessionName.setText(String.join(" ",
                upcomingSession.getEventName(),
                "-",
                upcomingSession.getSessionName()));
        holder.mViewRacetrack.setText(upcomingSession.getRacetrack());
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(upcomingSession.getSessionStartTime()),
                ZoneId.systemDefault()
        );
        LocalDateTime endTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(upcomingSession.getSessionEndTime()),
                ZoneId.systemDefault()
        );
        holder.mViewSessionHours.setText(
                String.join((" "),
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime),
                        " - ",
                        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(endTime)
        ));

        Glide.with(holder.mView)
                .load(upcomingSession.getSeriesLogo())
                //.override(150)
                .centerInside()
                .into(holder.mViewSeriesLogo);
    }

    @Override
    public int getItemCount() {
        return upcomingSessions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mViewSessionHours;
        public final TextView mViewSessionName;
        public final TextView mViewRacetrack;
        public final ImageView mViewSeriesLogo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mViewSessionHours = (TextView) view.findViewById(R.id.textViewSessionHours);
            mViewSessionName = (TextView) view.findViewById(R.id.textViewSessionName);
            mViewRacetrack = (TextView) view.findViewById(R.id.textViewRacetrack);
            mViewSeriesLogo = (ImageView) view.findViewById(R.id.imageViewSeriesLogo);
        }

    }
}
