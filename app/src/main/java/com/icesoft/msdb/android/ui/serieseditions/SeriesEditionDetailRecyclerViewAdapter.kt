package com.icesoft.msdb.android.ui.serieseditions

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionDetailItemBinding
import com.icesoft.msdb.android.model.EventEdition
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class SeriesEditionDetailRecyclerViewAdapter(private val values: List<EventEdition>):
    RecyclerView.Adapter<SeriesEditionDetailRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentSeriesEditionDetailItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMMMMyyyy");
        val dateFormat = SimpleDateFormat(pattern);
        holder.eventDateTextView.text = dateFormat.format(
            Date.from(item.getEventDate()?.atStartOfDay(ZoneId.systemDefault())?.toInstant()))
        holder.eventNameTextView.text = item.longEventName
        Picasso.get().load(item.trackLayout?.layoutImageUrl)
            .resize(140, 0)
            .centerInside()
            .into(holder.racetrackImageView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSeriesEditionDetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val eventDateTextView: TextView = binding.eventEditionDate
        val eventNameTextView: TextView = binding.eventEditionName
        val racetrackImageView: ImageView = binding.eventEditionRacetrack
    }
}
