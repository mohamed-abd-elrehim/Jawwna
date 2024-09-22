package com.example.jawwna.homescreen


import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jawwna.BuildConfig
import com.example.jawwna.R
import com.example.jawwna.databinding.FragmentHomeBinding
import com.example.jawwna.homescreen.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Access the VideoView using View Binding
        // Get current night mode
        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK


        viewModel.setCardSettingsFieldBackgroundLightMode(
            requireContext().packageName,
            nightModeFlags
        )
        viewModel.cardSettingsFieldBackgroundLightModeLiveData.observe(
            viewLifecycleOwner,
            Observer { colorResId ->
                binding.linearLayoutMainCard.setBackgroundResource(colorResId)
                binding.dayForecastLinearlayout1.setBackgroundResource(colorResId)
                binding.dayForecastLinearlayout2.setBackgroundResource(colorResId)
                binding.dayForecastLinearlayout3.setBackgroundResource(colorResId)
                binding.dayForecastLinearlayout4.setBackgroundResource(colorResId)
                binding.dayForecastLinearlayout5.setBackgroundResource(colorResId)
                binding.dayForecastLinearlayout6.setBackgroundResource(colorResId)
                binding.constraintLayoutWeatherInformation.setBackgroundResource(colorResId)


            })

        // Observe the theme mode from ViewModel
        viewModel.isDarkMode.observe(viewLifecycleOwner) { isDarkMode ->
            updateIcons(isDarkMode)
            updateTextColor(isDarkMode)

        }

        // Check the current theme mode when view is created
        viewModel.checkThemeMode(resources)
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
         binding.dayForecastTextDes1.setTextColor(textColor)
        binding.dayForecastTextDes2.setTextColor(textColor)
        binding.dayForecastTextDes3.setTextColor(textColor)
        binding.dayForecastTextDes4.setTextColor(textColor)
        binding.dayForecastTextDes5.setTextColor(textColor)
        binding.dayForecastTextDes6.setTextColor(textColor)

        binding.dayForecastTextTemperature1.setTextColor(textColor)
        binding.dayForecastTextTemperature2.setTextColor(textColor)
        binding.dayForecastTextTemperature3.setTextColor(textColor)
        binding.dayForecastTextTemperature4.setTextColor(textColor)
        binding.dayForecastTextTemperature5.setTextColor(textColor)
        binding.dayForecastTextTemperature6.setTextColor(textColor)
        binding.textWindSpeed.setTextColor(textColor)
        binding.textHumidity.setTextColor(textColor)
        binding.textPressure.setTextColor(textColor)
        binding.textClouds.setTextColor(textColor)
    }


}
