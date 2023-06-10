package com.icesoft.msdb.android.ui.serieseditions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.icesoft.msdb.android.model.SeriesEdition

class SeriesEditionDetailViewModel : ViewModel() {

    private val seriesEditions = MutableLiveData<List<SeriesEdition>>()

    fun setSeriesEditions(sessions: List<SeriesEdition>) {
        this.seriesEditions.value = sessions
    }

    fun getSeriesEditions(): LiveData<List<SeriesEdition>>? {
        return seriesEditions
    }
}