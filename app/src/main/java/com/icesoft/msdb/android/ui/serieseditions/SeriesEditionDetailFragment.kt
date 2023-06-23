package com.icesoft.msdb.android.ui.serieseditions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.model.EventEdition
import com.icesoft.msdb.android.service.MSDBService
import com.icesoft.msdb.android.tasks.GetSeriesEditionEventsTask
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class SeriesEditionDetailFragment : Fragment() {
    private lateinit var cache: LoadingCache<Long, List<EventEdition>>
    private lateinit var msdbService: MSDBService
    private var serviceBound = false

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val seriesEditionId = arguments?.getLong("seriesEditionId")!!
        val view = inflater.inflate(R.layout.fragment_series_edition_detail, container, false)

        cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build { id ->
                val doneSignal = CountDownLatch(1)
                val getSeriesEditionEventsTask = GetSeriesEditionEventsTask(id, doneSignal)
                val executor = Executors.newFixedThreadPool(1)
                val opSeriesEditionEvents: Future<List<EventEdition>> =
                    executor.submit(getSeriesEditionEventsTask)

                // All this smells as too much Java approach. Should properly learn coroutines, suspend and so
                doneSignal.await()
                opSeriesEditionEvents.get()
            }

        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.series_edition_events_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SeriesEditionDetailRecyclerViewAdapter(
            cache.get(seriesEditionId) ?: Collections.emptyList())

        val textView = view.findViewById<TextView>(R.id.series_edition_name)
        textView.text = arguments?.getString("seriesEditionName")

        return view
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), MSDBService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}