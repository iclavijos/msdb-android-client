package com.icesoft.msdb.android.ui.eventdetails;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.EventEdition;
import com.icesoft.msdb.android.model.RacetrackLayout;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EventDetailsInfoFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        EventDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);
        EventEdition eventDetails = viewModel.getEventEdition();

        // Inflate the layout for this fragment
        View eventDetailsView = inflater.inflate(R.layout.fragment_event_details_info, container, false);

        TextView eventDateTextView = eventDetailsView.findViewById(R.id.eventDetailsEventDateTextView);
        eventDateTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(eventDetails.getEventDate()));

        TextView racetrackNameTextView = eventDetailsView.findViewById(R.id.racetrackNameEventDetailsTextView);
        Optional<RacetrackLayout> optTrackLayout = Optional.ofNullable(eventDetails.getTrackLayout());
        optTrackLayout.ifPresent(racetrackLayout -> {
            racetrackNameTextView.setText(racetrackLayout.getRacetrack().getName());

            ImageView trackLayoutImageview = eventDetailsView.findViewById(R.id.racetrackLayoutEventDetailsImageView);
            Glide.with(eventDetailsView)
                    .load(racetrackLayout.getLayoutImageUrl())
                    //.override(150)
                    .centerInside()
                    .dontAnimate()
                    .into(trackLayoutImageview);
        });
        Optional.ofNullable(eventDetails.getPosterUrl()).ifPresent(posterUrl -> {
            ImageView posterImageView = eventDetailsView.findViewById(R.id.posterImageView);
            if (posterImageView != null) {
                Glide.with(eventDetailsView)
                        .load(posterUrl)
                        .centerInside()
                        .dontAnimate()
                        .into(posterImageView);
            }
        });
        if (!optTrackLayout.isPresent()) {
            TextView racetrackLabel = eventDetailsView.findViewById(R.id.racetrackLabel);
            racetrackLabel.setText(eventDetails.getLocation());
            ImageView trackLayoutImageview = eventDetailsView.findViewById(R.id.racetrackLayoutEventDetailsImageView);
            trackLayoutImageview.setVisibility(View.GONE);
        }

        return eventDetailsView;
    }

    private boolean isDarkMode() {
        int uiMode = this.getResources().getConfiguration().uiMode;

        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}