package com.icesoft.msdb.android.tasks

import com.icesoft.msdb.android.model.EventEdition
import com.icesoft.msdb.android.service.BackendService
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch

class GetSeriesEditionEventsTask(
    private val seriesEditionId: Long,
    private val doneSignal: CountDownLatch): Callable<List<EventEdition>> {
    override fun call(): List<EventEdition> {
        val events = BackendService.getInstance().getSeriesEditionEvents(seriesEditionId)
        doneSignal.countDown()
        return events
    }
}