package com.icesoft.msdb.android.ui.serieseditions

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionDetailBinding
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionsBinding

class SeriesEditionDetailFragment : Fragment() {

    companion object {
        fun newInstance() = SeriesEditionDetailFragment()
    }

    private var _binding: FragmentSeriesEditionDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: SeriesEditionDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesEditionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SeriesEditionDetailViewModel::class.java)
        val editionId = arguments?.getLong("seriesEditionId")
        binding.textViewDummy.text = editionId.toString()
    }

    override fun onResume() {
        super.onResume()
        // see bools.xml resource file
        // used to easily find device's orientation
//        if (activity?.resources?.getBoolean(R.bool.isLandscape) == true) {
//            findNavController().navigateUp()
//        }
    }
}