package com.icesoft.msdb.android.ui.serieseditions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.adapter.SeriesEditionAdapter
import com.icesoft.msdb.android.model.EventEdition
import com.icesoft.msdb.android.model.SeriesEdition
import com.icesoft.msdb.android.service.MSDBService
import com.icesoft.msdb.android.tasks.GetSeriesEditionEventsTask
import com.icesoft.msdb.android.tasks.GetSeriesEditionsTask
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SeriesEditionDetailFragment : Fragment(), AdapterView.OnItemSelectedListener, View.OnTouchListener {
    private lateinit var msdbService: MSDBService
    private var serviceBound = false

    private lateinit var seriesEditions: List<SeriesEdition>
    private lateinit var adapter: SeriesEditionDetailRecyclerViewAdapter
    private var userSelect = false

    private val executor = Executors.newFixedThreadPool(1)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MSDBService.LocalBinder
            msdbService = binder.getService()
            val recyclerView = view?.findViewById<RecyclerView>(R.id.series_edition_events_recycler)
            val adapter = recyclerView?.adapter as SeriesEditionDetailRecyclerViewAdapter
            adapter.msdbService = msdbService
            serviceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), MSDBService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(connection)
        serviceBound = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val seriesId = arguments?.getLong("seriesId")!!
        val view = inflater.inflate(R.layout.fragment_series_edition_detail, container, false)

        val editionSpinner: Spinner = view.findViewById(R.id.year_selector_spinner)
        val recyclerView = view.findViewById<RecyclerView>(R.id.series_edition_events_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        var currentEditionIndex = 0
        if (seriesId == 0L) {
            // Landscape mode, so no series initially selected
            seriesEditions = Collections.emptyList()
            adapter = SeriesEditionDetailRecyclerViewAdapter(Collections.emptyList())
        } else {
            val doneSignal = CountDownLatch(1)
            val getSeriesEditionsTask = GetSeriesEditionsTask(seriesId, doneSignal)
            val opSeriesEditions: Future<List<SeriesEdition>> =
                executor.submit(getSeriesEditionsTask)
            doneSignal.await()
            seriesEditions =
                opSeriesEditions.get().sortedByDescending(SeriesEdition::periodEnd)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentEdition = seriesEditions
                .firstOrNull { edition -> edition.periodEnd == currentYear.toString() }
                ?: seriesEditions.first()
            currentEditionIndex = seriesEditions.indexOf(currentEdition)

            // Set the adapter
            adapter = SeriesEditionDetailRecyclerViewAdapter(getEditionEvents(currentEdition))
        }

        recyclerView.adapter = adapter

        editionSpinner.setOnTouchListener(this)
        editionSpinner.onItemSelectedListener = this
        editionSpinner.adapter = SeriesEditionAdapter(requireContext(), seriesEditions)
        editionSpinner.setSelection(currentEditionIndex, true)

        return view
    }

    private fun getEditionEvents(currentEdition: SeriesEdition): List<EventEdition> {
        val doneSignal = CountDownLatch(1)
        val getSeriesEditionEventsTask = GetSeriesEditionEventsTask(currentEdition.id!!, doneSignal)
        val opEditionEvents: Future<List<EventEdition>> = executor.submit(getSeriesEditionEventsTask)
        doneSignal.await()
        return opEditionEvents.get().filter { edition ->
            !edition.status.equals("cancelled", true)
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        userSelect = true
        return false
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if (userSelect) {
            val seriesEdition = parent.getItemAtPosition(pos) as SeriesEdition
            adapter.updateEventsList(getEditionEvents(seriesEdition))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        println("Nothing selected")
    }
}
