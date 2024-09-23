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
import com.example.jawwna.BuildConfig
import com.example.jawwna.R
import com.example.jawwna.databinding.FragmentMapBinding
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.mapscreen.viewmodel.MapViewModel
import com.example.jawwna.mapscreen.viewmodel.MapViewModelFactory
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModel
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModelFactory
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

        // Initialize mapViewModel using ViewModelProvider
        mapViewModel = ViewModelProvider(this, MapViewModelFactory(requireActivity().application,
            Repository.getRepository())).get(
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
        mapViewModel.setCardSettingsFieldBackgroundLightMode(requireContext().packageName, nightModeFlags)
        mapViewModel.cardSettingsFieldBackgroundLightModeLiveData.observe(viewLifecycleOwner, Observer { colorResId ->
            binding.searchLocation.setBackgroundResource(colorResId) // For SearchView background
            binding.saveLocationButton.setBackgroundResource(colorResId) // For Button background
        })

        // Observe and set icon for FloatingActionButton
        mapViewModel.setIcon(requireContext().packageName, nightModeFlags)
        mapViewModel.icon.observe(viewLifecycleOwner, Observer { iconResId ->
            binding.saveLocationButton.setImageResource(iconResId) // Assuming saveLocationButton is a FloatingActionButton
        })


        //mapViewModel.updateLocation(30.0444, 31.2357)

    }


    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true //Zoom en el mapa
        // Observe changes in the location LiveData from ViewModel
        mapViewModel.location.observe(viewLifecycleOwner, Observer { location ->
            getCountryNameFromLatLong(requireContext(), location.latitude, location.longitude)

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
            googleMap.addMarker(MarkerOptions().position(latLng).title(""))
            // Do something with the latitude and longitude
            // For example, update the ViewModel with the new location
            mapViewModel.updateLocation(latLng.latitude, latLng.longitude)
            Log.i(TAG, ": " + latLng.latitude + " " + latLng.longitude)


        }
        lifecycleScope.launch {
            mapViewModel.weatherData.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        // Show loading indicator
                    }
                    is ApiResponse.Success -> {
                        // Update UI with the weather data
                        Toast.makeText(requireContext(), "Weather: ${response.data}", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, ": Sccesss"+response.data)

                    }
                    is ApiResponse.Error -> {
                        // Show error message
                        val alertDialog = AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(response.message)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            mapViewModel.weatherData2.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        // Show loading indicator
                    }
                    is ApiResponse.Success -> {
                        // Update UI with the weather data
                        Toast.makeText(requireContext(), "Weather: ${response.data}", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, ": Sccesss2"+response.data)

                    }
                    is ApiResponse.Error -> {
                        // Show error message
                        val alertDialog = AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(response.message)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }
            }
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
            if (currentPlaceName != null && googleMap.cameraPosition.target != null) {
                val latitude = googleMap.cameraPosition.target.latitude
                val longitude = googleMap.cameraPosition.target.longitude

                // Save the location data using ViewModel
                mapViewModel.saveLocationData(currentPlaceName!!, latitude, longitude)
                Toast.makeText(
                    requireContext(),
                    "Location saved: $currentPlaceName",
                    Toast.LENGTH_SHORT
                ).show()

                // Fetch weather data


                // Trigger fetching weather data
                fetchWeatherData  ( latitude, longitude)
                fetchWeatherData2  ( latitude, longitude)
            } else {
                Log.e(TAG, "Current location or place name is not set.")
            }
        }

    }
    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val apiKey = BuildConfig.OPEN_WEATHER_API_KEY_PRO
//       val latitude = 0.0 // Replace with your latitude
//       val longitude = 0.0 // Replace with your longitude
       mapViewModel.fetchWeatherData(apiKey, latitude, latitude)
    }
    private fun fetchWeatherData2(latitude: Double, longitude: Double) {
        val apiKey = BuildConfig.OPEN_WEATHER_API_KEY_PRO
//       val latitude = 0.0 // Replace with your latitude
//       val longitude = 0.0 // Replace with your longitude
        mapViewModel.fetchWeatherData2(apiKey, latitude, latitude)
    }
    private fun showLocationErrorDialog() {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Location Error")
            .setMessage("The selected location is not compatible with the API. Please choose another location.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Choose Location") { dialog, _ ->
                dialog.dismiss()
                // Handle redirection to location selection logic here
            }
            .create()

        alertDialog.show()
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


    fun getCountryNameFromLatLong(context: Context, latitude: Double, longitude: Double): String?
    {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            // Fetch the address list using the geocoder
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            // If the address list is not empty, return the country name
            if (!addresses.isNullOrEmpty()) {
                Toast.makeText(context, addresses[0].countryName, Toast.LENGTH_SHORT).show()

                val address = addresses[0]
                 addresses[0].countryName
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
