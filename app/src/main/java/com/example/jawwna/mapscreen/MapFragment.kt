// MapFragment.kt
package com.example.jawwna.mapscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.jawwna.R
import com.example.jawwna.databinding.FragmentMapBinding
import com.example.jawwna.mapscreen.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {
    private val TAG = "MapFragment"
    private val DEFAULT_ZOOM = 15f
    private var currentPlaceName: String? = null


    // View binding property
    lateinit var binding: FragmentMapBinding
    // ViewModel for MapFragment
    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the map style based on night mode

        // Initialize the MapFragment and bind it with the OnMapReadyCallback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        mapViewModel.getMapStyle(requireContext().packageName,nightModeFlags)



        //mapViewModel.updateLocation(30.0444, 31.2357)

    }



    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true //Zoom en el mapa
        // Observe changes in the location LiveData from ViewModel
        mapViewModel.location.observe(viewLifecycleOwner, Observer { location ->
            googleMap.clear() // Clear any existing markers
            googleMap.addMarker(MarkerOptions().position(location).title(currentPlaceName))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
        })
        mapViewModel.placeName.observe(viewLifecycleOwner, Observer { placeName ->
            currentPlaceName = placeName
        })

        // Observe mapMode changes and set the map style
        mapViewModel.mapMode.observe(viewLifecycleOwner, Observer { mapMode ->
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),mapMode))
        })



        // Optional: Handle map clicks to get location
        googleMap.setOnMapClickListener { latLng ->
            // Example: Set a marker on the clicked location and get the coordinates
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Cairo"))
            // Do something with the latitude and longitude
            // For example, update the ViewModel with the new location
            mapViewModel.updateLocation(latLng.latitude, latLng.longitude)
            Log.i(TAG, ": "+ latLng.latitude + " " + latLng.longitude)


        }
    }

}
