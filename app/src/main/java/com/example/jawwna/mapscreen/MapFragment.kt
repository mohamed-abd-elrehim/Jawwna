// MapFragment.kt
package com.example.jawwna.mapscreen

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
    lateinit var search_for_place: SearchView


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

        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        mapViewModel.getMapStyle(requireContext().packageName, nightModeFlags)

        // Initialize SearchView
        search_for_place = binding.searchForPlace
        search_for_place.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle the search query
                if (query != null) {
                    searchPlace(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optional: Handle text changes
                return true
            }
        })

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
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), mapMode))
        })


        // Optional: Handle map clicks to get location
        googleMap.setOnMapClickListener { latLng ->
            // Example: Set a marker on the clicked location and get the coordinates
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Cairo"))
            // Do something with the latitude and longitude
            // For example, update the ViewModel with the new location
            mapViewModel.updateLocation(latLng.latitude, latLng.longitude)
            Log.i(TAG, ": " + latLng.latitude + " " + latLng.longitude)


        }
    }





    private fun searchPlace(query: String) {
        // Implement your place search logic here
        // Example: Use Geocoder to get the address for the query
        val geocoder = Geocoder(requireContext())
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(query, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                if (address != null) {
                    val latLng = LatLng(address.latitude, address.longitude)
                    googleMap.clear() // Clear any existing markers
                    googleMap.addMarker(MarkerOptions().position(latLng).title(query))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                } else {
                    // Handle the case where the address is null
                    Log.e(TAG, "No address found for the query: $query")
                }
            } else {
                // Handle the case where no addresses are found
                Log.e(TAG, "No addresses found for the query: $query")
            }
        } catch (e: Exception) {
            // Handle any exceptions
            Log.e(TAG, "Geocoding failed: ${e.message}", e)
        }
    }




}