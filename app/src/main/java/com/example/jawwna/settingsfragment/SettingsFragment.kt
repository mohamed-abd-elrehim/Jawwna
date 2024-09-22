package com.example.jawwna.settingsfragment

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jawwna.R
import com.example.jawwna.databinding.ActivityMainBinding
import com.example.jawwna.databinding.FragmentMapBinding
import com.example.jawwna.databinding.FragmentSettingsBinding
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.WindSpeedUnits
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModel
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModelFactory


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    lateinit var viewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel =
            ViewModelProvider(this, SettingsViewModelFactory(requireActivity().application)).get(
                SettingsViewModel::class.java
            )


        //Observe getLocationMode changes
        viewModel.getLocationMode.observe(viewLifecycleOwner, Observer { mode ->
            when (mode) {
                "MAP" -> binding.radioMap.isChecked = true
                "GPS" -> binding.radioGps.isChecked = true
                else -> binding.radioMap.isChecked = true
            }
        })

        // Observe temperature unit changes
        viewModel.temperatureUnit.observe(viewLifecycleOwner, Observer { unit ->
            when (unit) {
                TemperatureUnits.standard.toString() -> binding.radioKelvin.isChecked = true
                TemperatureUnits.metric.toString() -> binding.radioCelsius.isChecked = true
                TemperatureUnits.imperial.toString() -> binding.radioFahrenheit.isChecked = true
                else -> binding.radioKelvin.isChecked = true
            }
        })
        // Observe language changes
        viewModel.language.observe(viewLifecycleOwner, Observer { language ->
            when (language) {
                "en" -> binding.radioEnglish.isChecked = true
                "ar" -> binding.radioArabic.isChecked = true
                else -> binding.radioEnglish.isChecked = true
            }
        })

        // Observe theme changes
        viewModel.theme.observe(viewLifecycleOwner, Observer { theme ->
            when (theme) {
                "light" -> binding.radioLight.isChecked = true
                "dark" -> binding.radioDark.isChecked = true
                else -> binding.radioLight.isChecked = true
            }
        })
        // Observe notification changes
        viewModel.notificationsStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                "enabled" -> binding.radioEnable.isChecked = true
                "disabled" -> binding.radioDisable.isChecked = true
                else -> binding.radioDisable.isChecked = true
            }
        })
        // Observe wind speed unit changes
        viewModel.windSpeedUnit.observe(viewLifecycleOwner, Observer { unit ->
            when (unit) {
                WindSpeedUnits.metric.toString() -> binding.radioMeterPerSec.isChecked = true
                WindSpeedUnits.imperial.toString() -> binding.radioMilesPerHour.isChecked = true
            }
        })



        // Get current night mode
        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        // Set the video URI in the ViewModel based on the night mode



        viewModel.setCardSettingsFieldBackgroundLightMode(
            requireContext().packageName,
            nightModeFlags
        )
        viewModel.cardSettingsFieldBackgroundLightModeLiveData.observe(
            viewLifecycleOwner,
            Observer { colorResId ->
                binding.locationBackground.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.temperatureBackground.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.windSpeedBackground.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.languageBackground.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.themeBackground.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.notificationsBackground.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.resettingbut.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.savebut.background = ContextCompat.getDrawable(requireContext(), colorResId)
                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                        binding.resettingbut.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextNightMode))
                        binding.savebut.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextNightMode))
                    }else{
                        binding.resettingbut.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorText))
                        binding.savebut.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorText))
                    }
            })

        binding.savebut.setOnClickListener {
            // Scale down animation
            binding.savebut.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    // Scale back up
                    binding.savebut.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }

            // Get the currently selected settings
            val locationMode = when (binding.radioGroupLocation.checkedRadioButtonId) {
                R.id.radio_map -> "MAP"
                R.id.radio_gps -> "GPS"
                else -> null
            }

            val temperatureUnit = when (binding.radioGroupTemperature.checkedRadioButtonId) {
                R.id.radio_kelvin -> TemperatureUnits.standard.toString()
                R.id.radio_celsius -> TemperatureUnits.metric.toString()
                R.id.radio_fahrenheit -> TemperatureUnits.imperial.toString()
                else -> null
            }

            val windSpeedUnit = when (binding.radioGroupWindSpeed.checkedRadioButtonId) {
                R.id.radio_meter_per_sec -> WindSpeedUnits.metric.toString()
                R.id.radio_miles_per_hour -> WindSpeedUnits.imperial.toString()
                else -> null
            }

            val language = when (binding.radioGroupLanguage.checkedRadioButtonId) {
                R.id.radio_arabic -> "ar"
                R.id.radio_english -> "en"
                else -> null
            }

            val theme = when (binding.radioGroupTheme.checkedRadioButtonId) {
                R.id.radio_light -> "light"
                R.id.radio_dark -> "dark"
                else -> null
            }

            val notificationsStatus = when (binding.notificationGroupTheme.checkedRadioButtonId) {
                R.id.radio_enable -> "enabled"
                R.id.radio_disable -> "disabled"
                else -> null
            }

            // Call updateSettings with the selected values
            updateSettings(locationMode, temperatureUnit, windSpeedUnit, language, theme, notificationsStatus)
            Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show()
        }

        binding.resettingbut.setOnClickListener {
            // Scale down animation
            binding.resettingbut.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    // Scale back up
                    binding.resettingbut.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }

            viewModel.resetSettings()
            Toast.makeText(requireContext(), "Settings reset", Toast.LENGTH_SHORT).show()

        }


    }

    // Method to update settings
    fun updateSettings(
        locationMode: String? = null,
        temperatureUnit: String? = null,
        windSpeedUnit: String? = null,
        language: String? = null,
        theme: String? = null,
        notificationsStatus: String? = null
    ) {
        viewModel.saveSettings(
            locationMode,
            temperatureUnit,
            windSpeedUnit,
            language,
            theme,
            notificationsStatus
        )
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {

            }
    }
}
