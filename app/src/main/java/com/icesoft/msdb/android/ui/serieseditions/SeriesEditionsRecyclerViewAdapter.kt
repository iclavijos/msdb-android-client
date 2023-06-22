package com.icesoft.msdb.android.ui.serieseditions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionsItemBinding
import com.icesoft.msdb.android.model.SeriesEdition

class SeriesEditionsRecyclerViewAdapter(private val values: List<SeriesEdition>):
    RecyclerView.Adapter<SeriesEditionsRecyclerViewAdapter.ViewHolder>() {

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
        val item = values[position]
        holder.itemView.setOnClickListener(View.OnClickListener { view ->
            view.findNavController().navigate(
                SeriesEditionsFragmentDirections
                    .actionEditionsListToEditionDetail(item.id!!, item.editionName!!))
        })
        holder.editionNameView.text = item.editionName
        Glide.with(holder.itemView)
            .load(item.logoUrl) //.override(150)
            .centerInside()
            .into(holder.logoImageView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSeriesEditionsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val editionNameView: TextView = binding.editionName
        val logoImageView: ImageView = binding.logo
    }
}
