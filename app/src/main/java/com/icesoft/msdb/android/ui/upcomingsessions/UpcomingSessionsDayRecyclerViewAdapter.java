package com.icesoft.msdb.android.ui.upcomingsessions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.result.Credentials;
import com.bumptech.glide.Glide;
import com.icesoft.msdb.android.activity.EventDetailsActivity;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.UpcomingSession;
import com.icesoft.msdb.android.service.MSDBService;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class UpcomingSessionsDayRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingSessionsDayRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "UpcomingSessionsDayViewAdapter";

    private final List<UpcomingSession> upcomingSessions;
    private MSDBService msdbService;

    public UpcomingSessionsDayRecyclerViewAdapter(Context context, List<UpcomingSession> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;

        Intent intent = new Intent(context, MSDBService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                MSDBService.LocalBinder binder = (MSDBService.LocalBinder) service;
                msdbService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
        holder.setUpcomingSession(upcomingSession);
        holder.mViewEventName.setText(upcomingSession.getEventName());
        holder.mViewSessionName.setText(upcomingSession.getSessionName());
        holder.mViewRacetrack.setText(upcomingSession.getRacetrack());
        if (!upcomingSession.isCancelled()) {
            holder.mViewCancelled.setVisibility(View.GONE);
        }
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(upcomingSession.getSessionStartTime()),
                ZoneId.systemDefault()
        );
        if (upcomingSession.isRally()) {
            holder.mViewSessionHours.setText(
                    String.join((" "),
                            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime),
                            " - ",
                            Objects.requireNonNull(upcomingSession.getDuration()).toString(),
                            "KM"
                    ));
        } else if (upcomingSession.isRaid()) {
            DecimalFormat df = new DecimalFormat("#");
            holder.mViewSessionHours.setText(
                    String.join((" "),
                            df.format(upcomingSession.getDuration()),
                            "/",
                            df.format(upcomingSession.getTotalDuration()),
                            "KM"
                    ));
        } else {
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
        }


        if (upcomingSession.getSeriesLogo() != null) {
            Glide.with(holder.mView)
                    .load(upcomingSession.getSeriesLogo())
                    //.override(150)
                    .centerInside()
                    .into(holder.mViewSeriesLogo);
        }
    }

    @Override
    public int getItemCount() {
        return upcomingSessions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private UpcomingSession upcomingSession;
        public final View mView;
        public final TextView mViewSessionHours;
        public final TextView mViewEventName;
        public final TextView mViewSessionName;
        public final TextView mViewRacetrack;
        public final ImageView mViewSeriesLogo;
        public final CardView mViewCancelled;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            mViewSessionHours = (TextView) view.findViewById(R.id.textViewSessionHours);
            mViewEventName = (TextView) view.findViewById(R.id.textViewEventName);
            mViewSessionName = (TextView) view.findViewById(R.id.textViewSessionName);
            mViewRacetrack = (TextView) view.findViewById(R.id.textViewRacetrack);
            mViewSeriesLogo = (ImageView) view.findViewById(R.id.imageViewSeriesLogo);
            mViewCancelled = view.findViewById(R.id.cancelledCardView);
        }

        protected void setUpcomingSession(UpcomingSession upcomingSession) {
            this.upcomingSession = upcomingSession;
        }

        @Override
        public void onClick(View v) {
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
                Intent intent = new Intent(mView.getContext(), EventDetailsActivity.class);
                intent.putExtra("eventEditionId", upcomingSession.getEventEditionId());
                intent.putExtra("eventName", upcomingSession.getEventName());
                intent.putExtra("accessToken", credentials.getAccessToken());
                mView.getContext().startActivity(intent);
            }
        }
    }
}
