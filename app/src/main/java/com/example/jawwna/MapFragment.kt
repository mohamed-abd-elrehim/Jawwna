package com.example.jawwna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.jawwna.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    // View binding property
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // ViewModel for MapFragment
    private val mapViewModel: MapViewModel by viewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        // Observe changes in the location LiveData from ViewModel
        mapViewModel.location.observe(viewLifecycleOwner, Observer { location ->
            googleMap.addMarker(MarkerOptions().position(location).title("Marker"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the MapFragment and bind it with the OnMapReadyCallback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
