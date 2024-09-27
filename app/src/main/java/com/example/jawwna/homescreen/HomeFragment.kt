package com.example.jawwna.homescreen


import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jawwna.BuildConfig
import com.example.jawwna.R
import com.example.jawwna.databinding.FragmentHomeBinding
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.homescreen.adapter.DailyWeatherForecastAdapter
import com.example.jawwna.homescreen.adapter.HourlyWeatherForecastAdapter
import com.example.jawwna.homescreen.viewmodel.HomeViewModelFactory
import com.example.jawwna.homescreen.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var hourlyRecyclerViewAdapter: HourlyWeatherForecastAdapter
    private lateinit var hourlyRecyclerView: RecyclerView


    private lateinit var daliyRecyclerViewAdapter: DailyWeatherForecastAdapter
    private lateinit var daliyRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(
                this,
                HomeViewModelFactory( Repository.getRepository(requireActivity().application))
            )[HomeViewModel::class.java]
        // Initialize the DailyWeatherForecastAdapter and RecyclerView
        daliyRecyclerView = binding.daliyRecyclerView
        // Set the LayoutManager for the RecyclerView
        daliyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Initialize the adapter with an empty list for now
        daliyRecyclerViewAdapter = DailyWeatherForecastAdapter(
            emptyList(),
            requireContext(),
            object : DailyWeatherForecastAdapter.OnItemClickListener {
                override fun onItemClick(forecast: com.example.jawwna.datasource.model.DailyForecastData) {
                    // Handle the item click, show a Toast or navigate to another screen
                    Toast.makeText(
                        requireContext(),
                        "Item clicked: ${forecast.dayName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        // Set the adapter to the RecyclerView
        daliyRecyclerView.adapter = daliyRecyclerViewAdapter

//_________________________________________________________________________

        // Initialize the DailyWeatherForecastAdapter and RecyclerView
        hourlyRecyclerView = binding.hourlyRecyclerView
        // Set the LayoutManager for the RecyclerView
        hourlyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize the adapter with an empty list for now
        hourlyRecyclerViewAdapter = HourlyWeatherForecastAdapter(
            emptyList(),
            requireContext(),
            object : HourlyWeatherForecastAdapter.OnItemClickListener {
                override fun onItemClick(forecast: com.example.jawwna.datasource.model.HourlyForecastData) {
                    // Handle the item click, show a Toast or navigate to another screen
                    Toast.makeText(
                        requireContext(),
                        "Item clicked: ${forecast.Time}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        // Set the adapter to the RecyclerView
        hourlyRecyclerView.adapter = hourlyRecyclerViewAdapter


        // Observe the LiveData from the ViewModel and update the adapter when data changes


        // Access the VideoView using View Binding
        // Get current night mode
        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK


        viewModel.setCardSettingsFieldBackgroundLightMode(
            requireContext().packageName,
            nightModeFlags
        )
        lifecycleScope.launch {



        viewModel.cardSettingsFieldBackgroundLightModeLiveData.collect{ colorResId ->
                binding.linearLayoutMainCard.setBackgroundResource(colorResId)
                binding.constraintLayoutWeatherInformation.setBackgroundResource(colorResId)


            }
        }
        lifecycleScope.launch {

        // Observe the theme mode from ViewModel
        viewModel.isDarkMode.collect{ isDarkMode ->
            updateIcons(isDarkMode)
            updateTextColor(isDarkMode)

        }
    }
        // Check the current theme mode when view is created
        viewModel.checkThemeMode(resources)
        viewModel.featch16DailyWeatherData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)

        lifecycleScope.launch {
            viewModel.weatherForecast16DailyRow.collect { data ->
                if (data.size == 7) {
                    daliyRecyclerViewAdapter.updateData(data)
                    Log.d(TAG, "onViewCreated: $data")
                } else {
                    Log.d(TAG, "No data received")

                }
            }


        }
viewModel.fetchWeatherForecastHourlyData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)
        lifecycleScope.launch {
            viewModel.weatherForecastHourlyRow.collect { data ->
                if (!data.isEmpty()){
                    hourlyRecyclerViewAdapter.updateData(data)
                    Log.d(TAG, "onViewCreated: $data")
                } else {
                    Log.d(TAG, "No data received")

                }
            }


        }

        viewModel.fetchCurrentWeatherData(BuildConfig.OPEN_WEATHER_API_KEY_PRO)

        lifecycleScope.launch {
            viewModel.currentWeatherData.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        // Show loading indicator
                    }

                    is ApiResponse.Success -> {
                        // Update UI with the weather data
                        Toast.makeText(
                            requireContext(),
                            "Weather: ${response.data}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(TAG, ": Sccesss Current" + response.data)
                        val result = viewModel.checkTemperatureUnit(response.data.main.temp)
                        binding.mainTextTemperature.text = getString(R.string.temperature_format, result.value, result.unit)

                        binding.textWindSpeed.text =viewModel.checkWindSpeedUnit(response.data.wind.speed)

                        binding.textHumidity.text = getString(R.string.wather_description,response.data.main.humidity)
                        binding.textPressure.text =  getString(R.string.wather_description,response.data.main.pressure)
                        binding.textClouds.text =  getString(R.string.wather_description,response.data.clouds.all)

                        binding.cityName.text = response.data.name


                        binding.mainTextDes.text = response.data.weather[0].description
                        binding.currentDate.text =viewModel.getCurrentDate()
                        binding.currentTime.text = viewModel.getCurrentTime()


                    }

                    is ApiResponse.Error -> {
                        // Show error message
                        val alertDialog = AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(response.message)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                        Log.i(TAG, ":ERROR Current " + response.message)
                    }
                }
            }
        }
        lifecycleScope.launch {

            delay(2000) // Delay for 2000 milliseconds (2 seconds)
            viewModel.insertWeatherResponseEntity() // Call the insert method
        }
    }

    private fun updateIcons(isDarkMode: Boolean) {
        if (isDarkMode) {
            // Set dark mode icons
            binding.iconHumidity.setImageResource(R.drawable.ic_humidity_night_mode)
            binding.iconWindSpeed.setImageResource(R.drawable.ic_windy_night_mode)
            binding.iconPressure.setImageResource(R.drawable.ic_barometer_night_mode)
            binding.iconClouds.setImageResource(R.drawable.ic_cloud_night_mode)
            binding.mainAnimation.setAnimation(R.raw.animationgg)
        } else {
            // Set light mode icons
            binding.iconHumidity.setImageResource(R.drawable.ic_humidity_light_mode)
            binding.iconWindSpeed.setImageResource(R.drawable.ic_windy_light_mode)
            binding.iconPressure.setImageResource(R.drawable.ic_barometer_light_mode)
            binding.iconClouds.setImageResource(R.drawable.ic_cloud_lgiht_mode)
            binding.mainAnimation.setAnimation(R.raw.animationgg)
        }
    }

    private fun updateTextColor(isDarkMode: Boolean) {
        val textColor = ContextCompat.getColor(requireContext(), viewModel.getTextColor(isDarkMode))
        binding.mainTextTemperature.setTextColor(textColor)
        binding.textWindSpeed.setTextColor(textColor)
        binding.textHumidity.setTextColor(textColor)
        binding.textPressure.setTextColor(textColor)
        binding.textClouds.setTextColor(textColor)
    }


}
