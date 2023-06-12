package com.icesoft.msdb.android.ui.serieseditions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.model.EventEdition
import com.icesoft.msdb.android.tasks.GetSeriesEditionEventsTask
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SeriesEditionDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val seriesEditionId = arguments?.getLong("seriesEditionId")
        val view = inflater.inflate(R.layout.fragment_series_edition_detail, container, false)

        val doneSignal = CountDownLatch(1)
        val getSeriesEditionEventsTask = GetSeriesEditionEventsTask(seriesEditionId!!, doneSignal)
        val executor = Executors.newFixedThreadPool(1)
        val opSeriesEditionEvents: Future<List<EventEdition>> =
            executor.submit(getSeriesEditionEventsTask)

        doneSignal.await()
        val events = opSeriesEditionEvents.get()

        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.series_edition_events_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SeriesEditionDetailRecyclerViewAdapter(events)

        val textView = view.findViewById<TextView>(R.id.series_edition_name)
        textView.text = arguments?.getString("seriesEditionName")

        return view
    }
}