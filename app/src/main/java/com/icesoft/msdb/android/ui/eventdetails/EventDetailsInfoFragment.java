package com.icesoft.msdb.android.ui.eventdetails;

import android.content.res.Configuration;
import android.os.Bundle;

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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EventDetailsInfoFragment extends Fragment {

    private EventEdition eventDetails;
    private EventDetailsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);
        eventDetails = viewModel.getEventEdition();

        // Inflate the layout for this fragment
        View eventDetailsView = inflater.inflate(R.layout.fragment_event_details_info, container, false);

        TextView eventDateTextView = eventDetailsView.findViewById(R.id.eventDetailsEventDateTextView);
        eventDateTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(eventDetails.getEventDate()));

        TextView racetrackNameTextView = eventDetailsView.findViewById(R.id.racetrackNameEventDetailsTextView);
        racetrackNameTextView.setText(eventDetails.getTrackLayout().getRacetrack().getName());

        ImageView trackLayoutImageview = eventDetailsView.findViewById(R.id.racetrackLayoutEventDetailsImageView);
        String layoutUrl = eventDetails.getTrackLayout().getLayoutUrl();
        if (isDarkMode()) {
            layoutUrl = layoutUrl.replace("image/upload", "image/upload/e_negate");
        }
        Glide.with(eventDetailsView)
                .load(layoutUrl)
                //.override(150)
                .centerInside()
                .dontAnimate()
                .into(trackLayoutImageview);

        return eventDetailsView;
    }

    private boolean isDarkMode() {
        int uiMode = this.getResources().getConfiguration().uiMode;

        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}