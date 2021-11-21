package com.icesoft.msdb.android.ui.eventdetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.EventSession;
import com.icesoft.msdb.android.tasks.GetEventSessionsTask;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParticipantsRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "ParticipantsRecyclerViewAdapter";

    private final String accessToken;
    private final Long eventEditionId;
    private List<EventSession> sessions = Collections.emptyList();

    public ParticipantsRecyclerViewAdapter(String accessToken, Long eventEditionId) {
        this.accessToken = accessToken;
        this.eventEditionId = eventEditionId;

//        GetEventSessionsTask task = new GetEventSessionsTask(accessToken, eventEditionId);
//        Future<List<EventSession>> opResult = Executors.newFixedThreadPool(1).submit(task);
//        try {
//            sessions = opResult.get();
//        } catch (ExecutionException | InterruptedException e) {
//            Log.e(TAG, "Couldn't retrieve event sessions", e);
//        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event_details_participants, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventSession session = sessions.get(position);
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(session.getStartTime()),
                ZoneId.systemDefault()
        );
        holder.startTimeTextView.setText(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(startTime));
        holder.sessionNameTextView.setText(session.getName());
        holder.durationTextView.setText(session.getDuration().toString());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EventSession session;
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

        protected void setEventSession(EventSession session) {
            this.session = session;
        }
    }
}