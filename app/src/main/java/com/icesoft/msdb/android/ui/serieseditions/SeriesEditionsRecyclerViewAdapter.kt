package com.icesoft.msdb.android.ui.serieseditions

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionsItemBinding
import com.icesoft.msdb.android.model.SeriesEdition
import java.util.*

class SeriesEditionsRecyclerViewAdapter(
    private val values: List<SeriesEdition>,
    private val detailViewContainer: View?):
    RecyclerView.Adapter<SeriesEditionsRecyclerViewAdapter.ViewHolder>() {

    private val filteredValues: MutableLiveData<List<SeriesEdition>> = MutableLiveData(values)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentSeriesEditionsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredValues.value?.get(position)!!

        holder.itemView.setOnClickListener { view ->
            if (detailViewContainer != null) {
                val bundle = Bundle()
                bundle.putLong("seriesEditionId", item.id!!)
                bundle.putString("seriesEditionName", item.editionName)
                detailViewContainer.findNavController()
                    .navigate(R.id.nav_championship_detail, bundle)
            } else {
                view.findNavController().navigate(
                    SeriesEditionsFragmentDirections
                        .actionEditionsListToEditionDetail(item.id!!, item.editionName!!)
                )
            }
        }
        holder.editionNameView.text = item.editionName
        Glide.with(holder.itemView)
            .load(item.logoUrl) //.override(150)
            .centerInside()
            .into(holder.logoImageView)
    }

    override fun getItemCount(): Int = filteredValues.value?.size ?: 0

    fun filterSeries(filter: String) {
        filteredValues.value
        if (TextUtils.isEmpty(filter)) {
            filteredValues.value = values
            return
        }
        filteredValues.value = values.filter {
            seriesEdition -> seriesEdition.editionName!!.lowercase(
                Locale.getDefault()
            ).contains(filter.lowercase(Locale.getDefault()))
        }
    }

    inner class ViewHolder(binding: FragmentSeriesEditionsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val editionNameView: TextView = binding.editionName
        val logoImageView: ImageView = binding.logo
    }
}
