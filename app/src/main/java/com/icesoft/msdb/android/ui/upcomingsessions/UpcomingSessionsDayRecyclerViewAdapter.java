package com.icesoft.msdb.android.ui.upcomingsessions;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.bumptech.glide.Glide;
import com.icesoft.msdb.android.activity.EventDetailsActivity;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.UpcomingSession;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class UpcomingSessionsDayRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingSessionsDayRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "UpcomingSessionsDayViewAdapter";

    private final List<UpcomingSession> upcomingSessions;
    private final SecureCredentialsManager credentialsManager;

    public UpcomingSessionsDayRecyclerViewAdapter(List<UpcomingSession> upcomingSessions, SecureCredentialsManager credentialsManager) {
        this.upcomingSessions = upcomingSessions;
        this.credentialsManager = credentialsManager;
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

//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if(manager instanceof LinearLayoutManager && getItemCount() > 0) {
//            LinearLayoutManager llm = (LinearLayoutManager) manager;
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                }
//
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    int visiblePosition = llm.findFirstCompletelyVisibleItemPosition();
//                    if(visiblePosition > -1) {
//                        View v = llm.findViewByPosition(visiblePosition);
//                        //do something
//                        v.setBackgroundColor(Color.parseColor("#777777"));
//                    }
//                }
//            });
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private UpcomingSession upcomingSession;
        public final View mView;
        public final TextView mViewSessionHours;
        public final TextView mViewSessionName;
        public final TextView mViewRacetrack;
        public final ImageView mViewSeriesLogo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            mViewSessionHours = (TextView) view.findViewById(R.id.textViewSessionHours);
            mViewSessionName = (TextView) view.findViewById(R.id.textViewSessionName);
            mViewRacetrack = (TextView) view.findViewById(R.id.textViewRacetrack);
            mViewSeriesLogo = (ImageView) view.findViewById(R.id.imageViewSeriesLogo);
        }

        protected void setUpcomingSession(UpcomingSession upcomingSession) {
            this.upcomingSession = upcomingSession;
        }

        @Override
        public void onClick(View v) {
            if (credentialsManager.hasValidCredentials()) {
                credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
                    @Override
                    public void onSuccess(final Credentials credentials) {
                        if (credentialsManager.hasValidCredentials()) {
                            Intent intent = new Intent(mView.getContext(), EventDetailsActivity.class);
                            intent.putExtra("eventEditionId", upcomingSession.getEventEditionId());
                            intent.putExtra("eventName", upcomingSession.getEventName());
                            intent.putExtra("accessToken", credentials.getIdToken());
                            mView.getContext().startActivity(intent);
                        } else {
                            Log.i(TAG, "No valid credentials...");
                        }
                    }

                    @Override
                    public void onFailure(CredentialsManagerException error) {
                        Log.w(TAG, "Couldn't get credentials");
                    }
                });
            }
        }
    }
}
