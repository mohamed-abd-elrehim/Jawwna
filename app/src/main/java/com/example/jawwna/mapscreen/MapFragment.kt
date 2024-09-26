// MapFragment.kt
package com.example.jawwna.mapscreen

import android.app.AlertDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jawwna.BuildConfig
import com.example.jawwna.R
import com.example.jawwna.add_favorite_location_screen.AddFavoriteLocationFragmentDirections
import com.example.jawwna.databinding.FragmentMapBinding
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.mapscreen.viewmodel.MapViewModel
import com.example.jawwna.mapscreen.viewmodel.MapViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Locale

class MapFragment : Fragment() {
    private val TAG = "MapFragment"
    private val DEFAULT_ZOOM = 15f

    private var currentPlaceName: String? = null
    lateinit var search_for_place: SearchView
    // View binding property
    lateinit var binding: FragmentMapBinding

    // ViewModel for MapFragment
    private lateinit var mapViewModel: MapViewModel
    private lateinit var googleMap: GoogleMap
    private val args: MapFragmentArgs by navArgs() //  Safe Args

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


        // Initialize mapViewModel using ViewModelProvider
        mapViewModel = ViewModelProvider(
            this, MapViewModelFactory(
                Repository.getRepository(requireActivity().application)
            )
        )[MapViewModel::class.java]

        // Initialize the MapFragment and bind it with the OnMapReadyCallback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback) // Ensure this callback is triggered first before using googleMap

        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        mapViewModel.getMapStyle(requireContext().packageName, nightModeFlags)

        // Initialize SearchView
        search_for_place = binding.searchLocation
        search_for_place.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchPlace(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })



        // Observe background color for SearchView and Button
        mapViewModel.setCardSettingsFieldBackgroundLightMode(requireContext().packageName, nightModeFlags)
        mapViewModel.setIcon(requireContext().packageName, nightModeFlags)

        lifecycleScope.launch {

            mapViewModel.cardSettingsFieldBackgroundLightModeLiveData.collect { colorResId ->
                binding.searchLocation.setBackgroundResource(colorResId)
                binding.saveLocationButton.setBackgroundResource(colorResId)
            }
        }

        // Observe icon for FloatingActionButton
        lifecycleScope.launch {
            mapViewModel.icon.collect { iconResId ->
                binding.saveLocationButton.setImageResource(iconResId)
            }
        }

        // Ensure this is called only after googleMap is initialized
        lifecycleScope.launch {
            mapViewModel.searchLocation.collect { latLng ->
                if (::googleMap.isInitialized) { // Check if googleMap is initialized
                    googleMap.clear()
                    // Fetch the place name based on coordinates
                    mapViewModel.fetchPlaceName(latLng.latitude, latLng.longitude)
                    Log.i(TAG, "onViewCreatedssss: "+ currentPlaceName)
                    googleMap.addMarker(MarkerOptions().position(latLng).title(currentPlaceName))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                }
            }
        }
    }


    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true //Zoom en el mapa
        // Observe changes in the location LiveData from ViewModel

        lifecycleScope.launch {
            mapViewModel.location.collect{ location ->
                googleMap.clear() // Clear any existing markers
                googleMap.addMarker(MarkerOptions().position(location).title(currentPlaceName))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
            }
        }


        lifecycleScope.launch {

            mapViewModel.placeName.collect() { placeName ->
                currentPlaceName = placeName
            }
        }
        lifecycleScope.launch {


            // Observe mapMode changes and set the map style
            mapViewModel.mapMode.collect { mapMode ->
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        mapMode
                    )
                )
            }
        }
        lifecycleScope.launch {
            mapViewModel.icon.collect{ iconResId ->
                binding.saveLocationButton.setImageResource(iconResId)
            }

        }


        // Optional: Handle map clicks to get location
        googleMap.setOnMapClickListener { latLng ->
            // Example: Set a marker on the clicked location and get the coordinates
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title(currentPlaceName))
            // Do something with the latitude and longitude
            // For example, update the ViewModel with the new location
            mapViewModel.updateLocation(latLng.latitude, latLng.longitude)
            Log.i(TAG, ": " + latLng.latitude + " " + latLng.longitude)


        }


        binding.saveLocationButton.setOnClickListener {
            // Scale down animation
            binding.saveLocationButton.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    // Scale back up
                    binding.saveLocationButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }

            // Check if place name and camera position are available
            if (currentPlaceName != null) {
                val latitude = googleMap.cameraPosition.target.latitude
                val longitude = googleMap.cameraPosition.target.longitude

                // Save the location data using ViewModel
                mapViewModel.saveLocationData(
                    currentPlaceName!!,
                    latitude,
                    longitude,
                    args.actionFav
                )

                Toast.makeText(
                    requireContext(),
                    "Location saved: $currentPlaceName",
                    Toast.LENGTH_SHORT
                ).show()


                when (args.actionFav) {
                    PreferencesLocationEum.FAVOURITE -> {
                        val action = MapFragmentDirections.actionMapFragmentToAddFavoriteLocationFragment(latitude.toFloat(), longitude.toFloat())
                        view?.findNavController()?.navigate(action)
                    }else -> {

                }
                }

            } else {
                Log.e(TAG, "Current location or place name is not set.")
            }
        }

    }





    private fun searchPlace(query: String) {
        // Implement your place search logic here
        // Example: Use Geocoder to get the address for the query

        mapViewModel.searchPlace(query)

    }





}
/*
       override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)
           // Set the map style based on night mode

           // Initialize mapViewModel using ViewModelProvider
           mapViewModel = ViewModelProvider(
               this, MapViewModelFactory(
                   Repository.getRepository(requireActivity().application)
               )
           ).get(
               MapViewModel::class.java
           )

           // Initialize the MapFragment and bind it with the OnMapReadyCallback
           val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
           mapFragment?.getMapAsync(callback)

           val nightModeFlags =
               resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
           mapViewModel.getMapStyle(requireContext().packageName, nightModeFlags)
           // Initialize SearchView
           search_for_place = binding.searchLocation
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

           // Observe and set background color for SearchView and Button
           mapViewModel.setCardSettingsFieldBackgroundLightMode(
               requireContext().packageName,
               nightModeFlags
           )
           mapViewModel.cardSettingsFieldBackgroundLightModeLiveData.observe(
               viewLifecycleOwner,
               Observer { colorResId ->
                   binding.searchLocation.setBackgroundResource(colorResId) // For SearchView background
                   binding.saveLocationButton.setBackgroundResource(colorResId) // For Button background
               })

           // Observe and set icon for FloatingActionButton
           mapViewModel.setIcon(requireContext().packageName, nightModeFlags)
           lifecycleScope.launch {
               mapViewModel.icon.collect { iconResId ->
                   binding.saveLocationButton.setImageResource(iconResId) // Assuming saveLocationButton is a FloatingActionButton
               }
           }
           lifecycleScope.launch {
               mapViewModel.searchLocation.collect { latLng ->
                   val latLng = latLng
                   googleMap.clear() // Clear any existing markers
                   mapViewModel.fetchPlaceName(latLng.latitude, latLng.longitude)
                   googleMap.addMarker(MarkerOptions().position(latLng).title(currentPlaceName))
                   googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                   googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))

               }
           }


           //mapViewModel.updateLocation(30.0444, 31.2357)

       }
   */