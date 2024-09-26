package com.example.jawwna.add_favorite_location_screen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jawwna.BuildConfig
import com.example.jawwna.R
import com.example.jawwna.add_favorite_location_screen.viewmodel.AddFavoriteLocationViewModel
import com.example.jawwna.add_favorite_location_screen.viewmodel.AddFavoriteLocationViewModelFactory
import com.example.jawwna.databinding.FragmentAddFavoriteLocationBinding
import com.example.jawwna.databinding.FragmentHomeBinding
import com.example.jawwna.databinding.FragmentMapBinding
import com.example.jawwna.datasource.model.FavoriteLocationModel
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.homescreen.adapter.DailyWeatherForecastAdapter
import com.example.jawwna.homescreen.adapter.FavoriteLocationAdapter
import com.example.jawwna.homescreen.adapter.HourlyWeatherForecastAdapter
import com.example.jawwna.homescreen.viewmodel.HomeViewModel
import com.example.jawwna.homescreen.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddFavoriteLocationFragment : Fragment() {

    private val TAG = "AddFavoriteLocationFragment"

    private lateinit var viewModel: AddFavoriteLocationViewModel
    lateinit var binding: FragmentAddFavoriteLocationBinding

    private lateinit var favoriteLocationAdapter: FavoriteLocationAdapter
    private lateinit var favoriteLocationRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFavoriteLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(
                this,
                AddFavoriteLocationViewModelFactory(Repository.getRepository(requireActivity().application))
            )[AddFavoriteLocationViewModel::class.java]


        // Initialize the DailyWeatherForecastAdapter and RecyclerView
        favoriteLocationRecyclerView = binding.recyclerViewFavorites
        // Set the LayoutManager for the RecyclerView
        favoriteLocationRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Initialize the adapter with an empty list for now
        favoriteLocationAdapter = FavoriteLocationAdapter(
            mutableListOf(),
            requireContext(),
            object : FavoriteLocationAdapter.OnItemClickListener {
                override fun onItemClick(favorites: FavoriteLocationModel) {
                    // Handle the item click, show a Toast or navigate to another screen
                    Toast.makeText(
                        requireContext(),
                        "Item clicked: ${favorites.placeName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, object : FavoriteLocationAdapter.OnDeleteItemClickListener {
                override fun onItemClick(favorites: FavoriteLocationModel) {
                    // Handle the item click, show a Toast or navigate to another screen
                    Toast.makeText(
                        requireContext(),
                        "Item clicked: ${favorites.placeName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        // Set the adapter to the RecyclerView
        favoriteLocationRecyclerView.adapter = favoriteLocationAdapter


        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK


        // Observe background color for SearchView and Button
        viewModel.setCardSettingsFieldBackgroundLightMode(
            requireContext().packageName,
            nightModeFlags
        )
        viewModel.setIcon(requireContext().packageName, nightModeFlags)

        lifecycleScope.launch {

            viewModel.cardSettingsFieldBackgroundLightModeLiveData.collect { colorResId ->
                binding.fabAddLocation.setBackgroundResource(colorResId)
            }
        }

        // Observe icon for FloatingActionButton
        lifecycleScope.launch {
            viewModel.icon.collect { iconResId ->
                binding.fabAddLocation.setImageResource(iconResId)
            }
        }

        viewModel.featch16DailyWeatherData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
        viewModel.fetchWeatherForecastHourlyData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
        viewModel.fetchCurrentWeatherData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
        viewModel.getAllFavoriteWeather()

        lifecycleScope.launch {
            viewModel.weatherFavoriteRow.collect { data ->
                favoriteLocationAdapter.updateData(data)
                Log.d(TAG, "Forecast list updated: $data")
            }
        }



            binding.fabAddLocation.setOnClickListener {
                Log.d(TAG, "onViewCreated: fabAddLocation")
                val action =
                    AddFavoriteLocationFragmentDirections.actionAddFavoriteLocationFragmentToMapFragment()
                view.findNavController().navigate(action)
            }


        lifecycleScope.launch {

            delay(2000) // Delay for 2000 milliseconds (2 seconds)
            viewModel.insertWeatherResponseEntity() // Call the insert method
        }

        }

    }










