package com.icesoft.msdb.android.ui.serieseditions

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.model.SeriesEdition
import com.icesoft.msdb.android.tasks.GetActiveSeriesTask
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class SeriesEditionsFragment : Fragment() {

    private lateinit var cache: LoadingCache<String, List<SeriesEdition>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_series_editions, container, false)

        cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build {
                val doneSignal = CountDownLatch(1)
                val getActiveSeriesTask = GetActiveSeriesTask(doneSignal)
                val executor = Executors.newFixedThreadPool(1)
                val opActiveSeries: Future<List<SeriesEdition>> =
                    executor.submit<List<SeriesEdition>>(getActiveSeriesTask)

                doneSignal.await()
                opActiveSeries.get()
            }

        // Set the adapter
        val itemDetailFragmentContainer: View? =
            view.findViewById(R.id.detail_fragment_container)
        val recyclerView = view.findViewById<RecyclerView>(R.id.series_editions_recycler)
        val orientation = resources.configuration.orientation
        recyclerView.layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }
        recyclerView.adapter = SeriesEditionsRecyclerViewAdapter(
            cache.get("seriesEditions") ?: Collections.emptyList(),
            itemDetailFragmentContainer
        )

        val filterEditText = view.findViewById<EditText>(R.id.filterSeriesEditText)
        filterEditText.addTextChangedListener {
            editable: Editable? ->
            run {
                if (editable != null) {
                    val adapter = recyclerView.adapter as SeriesEditionsRecyclerViewAdapter
                    adapter.filterSeries(editable.toString())
                    adapter.notifyDataSetChanged()
                }
            }
        }

        return view
    }

}