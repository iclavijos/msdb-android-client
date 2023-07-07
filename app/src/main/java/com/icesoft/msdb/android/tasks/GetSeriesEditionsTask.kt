package com.icesoft.msdb.android.tasks

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.icesoft.msdb.android.model.SeriesEdition
import com.icesoft.msdb.android.service.BackendService
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class GetSeriesEditionsTask(
    private val seriesId: Long,
    private val doneSignal: CountDownLatch): Callable<List<SeriesEdition>> {

    private val cache: Cache<Long, List<SeriesEdition>> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build()

    override fun call(): List<SeriesEdition> {
        val events = cache.get(seriesId) {
            BackendService.getInstance().getSeriesEditions(seriesId)
        }
        doneSignal.countDown()
        return events ?: Collections.emptyList()
    }
}