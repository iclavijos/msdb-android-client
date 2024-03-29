package com.icesoft.msdb.android.tasks

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.icesoft.msdb.android.model.EventEdition
import com.icesoft.msdb.android.service.BackendService
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class GetSeriesEditionEventsTask(
    private val seriesEditionId: Long,
    private val doneSignal: CountDownLatch): Callable<List<EventEdition>> {

    private val cache: Cache<Long, List<EventEdition>> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build()

    override fun call(): List<EventEdition> {
        val events = cache.get(seriesEditionId) {
            BackendService.getInstance().getSeriesEditionEvents(seriesEditionId)
        }
        doneSignal.countDown()
        return events ?: Collections.emptyList()
    }
}