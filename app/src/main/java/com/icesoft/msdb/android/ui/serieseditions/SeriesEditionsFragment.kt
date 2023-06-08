package com.icesoft.msdb.android.ui.serieseditions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.databinding.FragmentSeriesEditionsBinding

class SeriesEditionsFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = SeriesEditionsFragment()
    }

    private var _binding: FragmentSeriesEditionsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesEditionsBinding.inflate(inflater, container, false)
        binding.masterButton?.setOnClickListener(this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // true only in landscape
        if (binding.detailFragmentContainer != null) {
            childFragmentManager.beginTransaction()
                .replace(binding.detailFragmentContainer!!.id, SeriesEditionDetailFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // navigating to detail fragment
            binding.masterButton?.id -> {
                v?.findNavController()?.navigate(SeriesEditionsFragmentDirections.actionEditionsListToEditionDetail(2L))
            }
        }
    }

}