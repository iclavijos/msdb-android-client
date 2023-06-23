package com.icesoft.msdb.android.ui.serieseditions

import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icesoft.msdb.android.activity.EventDetailsActivity
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionDetailItemBinding
import com.icesoft.msdb.android.model.EventEdition
import com.icesoft.msdb.android.service.MSDBService
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class SeriesEditionDetailRecyclerViewAdapter(private val values: List<EventEdition>):
    RecyclerView.Adapter<SeriesEditionDetailRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "SeriesEditionDetailRecyclerViewAdapter"

    lateinit var msdbService: MSDBService

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
        holder.itemView.setOnClickListener(holder)
        holder.eventEdition = item
        val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMMMMyyyy")
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        holder.eventDateTextView.text = dateFormat.format(
            Date.from(item.getEventDate()?.atStartOfDay(ZoneId.systemDefault())?.toInstant()))
        holder.eventNameTextView.text = item.longEventName
        Glide.with(holder.itemView)
            .load(item.trackLayout?.layoutImageUrl ?: item.posterUrl)
            // .resize(140, 0)
            .centerInside()
            .into(holder.racetrackImageView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSeriesEditionDetailItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val eventDateTextView: TextView = binding.eventEditionDate
        val eventNameTextView: TextView = binding.eventEditionName
        val racetrackImageView: ImageView = binding.eventEditionRacetrack

        lateinit var eventEdition: EventEdition

        override fun onClick(view: View?) {
            if (msdbService.hasValidCredentials()) {
                val credentials = runBlocking { msdbService.getCredentials() }
                if (credentials != null) {
                    val intent = Intent(view!!.context, EventDetailsActivity::class.java)
                    intent.putExtra("eventEditionId", eventEdition.id)
                    intent.putExtra("eventName", eventEdition.longEventName)
                    intent.putExtra("accessToken", credentials.accessToken)
                    view.context.startActivity(intent)
                }
            }
        }
    }
}
