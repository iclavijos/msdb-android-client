package com.icesoft.msdb.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.model.SeriesEdition


class SeriesEditionAdapter(context: Context, seriesEditions: List<SeriesEdition>):
    ArrayAdapter<SeriesEdition>(context, R.layout.fragment_series_editions, seriesEditions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val customView =
            convertView ?:
            LayoutInflater.from(context).inflate(R.layout.series_edition_spinner, parent, false)

        val textViewName = customView.findViewById<TextView>(R.id.series_edition_spinner_series_name)
        val imageLogo = customView.findViewById<ImageView>(R.id.series_edition_spinner_logo)
        val currentItem: SeriesEdition? = getItem(position)

        if (currentItem != null) {
            textViewName.text = currentItem.editionName
            Glide.with(customView)
                .load(currentItem.logoUrl)
                .centerInside()
                .into(imageLogo)
        }
        return customView
    }
}