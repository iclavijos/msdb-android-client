package com.icesoft.msdb.android.ui.serieseditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.model.SeriesEdition
import com.icesoft.msdb.android.tasks.GetActiveSeriesTask
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SeriesEditionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_series_editions, container, false)

        val doneSignal = CountDownLatch(1)
        val getActiveSeriesTask = GetActiveSeriesTask(doneSignal)
        val executor = Executors.newFixedThreadPool(1)
        val opActiveSeries: Future<List<SeriesEdition>> =
            executor.submit<List<SeriesEdition>>(getActiveSeriesTask)

        doneSignal.await()
        val series = opActiveSeries.get()

        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.series_editions_recycler)
        recyclerView.layoutManager = GridLayoutManager(context, 2)// LinearLayoutManager(context)
        recyclerView.adapter = SeriesEditionsRecyclerViewAdapter(series)

        return view
    }

}