package com.example.guidemetro.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.example.guidemetro.databinding.FragmentStationBinding

class StationFragment : Fragment() {
    private var _binding: FragmentStationBinding? = null
    private val binding get() = _binding!!

    private var stationName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stationName = it.getString(ARG_STATION_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Header.text = stationName

        binding.iconButtonBack.setOnClickListener {
            replaceFragment(fragment = FragmentMap())
        }

        binding.iconButtonFav.setOnClickListener {

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    companion object {
        const val ARG_STATION_NAME = "station_name"

        fun newInstance(stationName: String) =
            StationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATION_NAME, stationName)
                }
            }
    }
}
