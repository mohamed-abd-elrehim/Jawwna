package com.example.jawwna.settingsfragment

import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.jawwna.R
import com.example.jawwna.customui.CustomAlertDialog
import com.example.jawwna.databinding.ActivityMainBinding
import com.example.jawwna.databinding.FragmentMapBinding
import com.example.jawwna.databinding.FragmentSettingsBinding
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.WindSpeedUnits
import com.example.jawwna.mainactivity.MainActivity
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModel
import com.example.jawwna.settingsfragment.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    lateinit var viewModel: SettingsViewModel
    private var isDarkMode = false
    private var isArabic = false
    private var isEnglish = false
    private var previousIsArabic = isArabic
    private var previousIsEnglish = isEnglish


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
            ViewModelProvider(
                this,
                SettingsViewModelFactory(Repository.getRepository(requireActivity().application))
            ).get(
                SettingsViewModel::class.java
            )

lifecycleScope.launch {
        //Observe getLocationMode changes
        viewModel.getLocationMode.collect{ mode ->
            when (mode) {
                "MAP" -> binding.radioMap.isChecked = true
                "GPS" -> binding.radioGps.isChecked = true
                else -> binding.radioMap.isChecked = true
            }
        }
}
        lifecycleScope.launch {

        // Observe temperature unit changes
        viewModel.temperatureUnit.collect { unit ->
            when (unit) {
                TemperatureUnits.standard.toString() -> binding.radioKelvin.isChecked = true
                TemperatureUnits.metric.toString() -> binding.radioCelsius.isChecked = true
                TemperatureUnits.imperial.toString() -> binding.radioFahrenheit.isChecked = true
                else -> binding.radioKelvin.isChecked = true
            }
        }}
        lifecycleScope.launch {
        // Observe language changes
        viewModel.language.collect { language ->
            when (language) {
                "en" -> binding.radioEnglish.isChecked = true
                "ar" -> binding.radioArabic.isChecked = true
                else -> binding.radioEnglish.isChecked = true
            }
        }}

        lifecycleScope.launch {
        // Observe theme changes
        viewModel.theme.collect { theme ->
            when (theme) {
                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    binding.radioLight.isChecked = true
                }
                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.radioDark.isChecked = true
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.radioLight.isChecked = true
                }
            }
        }}

        lifecycleScope.launch {
        // Observe wind speed unit changes
        viewModel.windSpeedUnit.collect { unit ->
            when (unit) {
                WindSpeedUnits.metric.toString() -> binding.radioMeterPerSec.isChecked = true
                WindSpeedUnits.imperial.toString() -> binding.radioMilesPerHour.isChecked = true
            }
        }}



        // Get current night mode
        val nightModeFlags =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            isDarkMode = true
        } else {
            isDarkMode = false

        }


        // Set the video URI in the ViewModel based on the night mode


        viewModel.setCardSettingsFieldBackgroundLightMode(
            requireContext().packageName,
            nightModeFlags
        )
        lifecycleScope.launch {
        viewModel.cardSettingsFieldBackgroundLightModeLiveData.collect { colorResId ->
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
                binding.resettingbut.background =
                    ContextCompat.getDrawable(requireContext(), colorResId)
                binding.savebut.background = ContextCompat.getDrawable(requireContext(), colorResId)
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    binding.resettingbut.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorTextNightMode
                        )
                    )
                    binding.savebut.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorTextNightMode
                        )
                    )
                } else {
                    binding.resettingbut.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorText
                        )
                    )
                    binding.savebut.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorText
                        )
                    )
                }
            }}

        binding.savebut.setOnClickListener {
            val customAlert = CustomAlertDialog(requireContext())

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

            when (binding.radioGroupLanguage.checkedRadioButtonId) {
                R.id.radio_arabic -> {
                    isArabic = true
                    isEnglish = false
                }

                R.id.radio_english -> {
                    isArabic = false
                    isEnglish = true
                }

                else -> null
            }
            val theme = when (binding.radioGroupTheme.checkedRadioButtonId) {
                R.id.radio_light -> "light"
                R.id.radio_dark -> "dark"
                else -> null
            }


            customAlert.showDialog(
                message = getString(R.string.are_you_sure),
                isDarkTheme = isDarkMode,
                positiveAction = {

                    // Call updateSettings with the selected values
                    updateSettings(
                        locationMode,
                        temperatureUnit,
                        windSpeedUnit,
                        language,
                        theme,
                    )
                    if ((previousIsArabic != isArabic) || (previousIsEnglish != isEnglish)) {
                        showRestartDialog(isDarkMode) // Show restart dialog only if language changed
                    }
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.settings_saved), Toast.LENGTH_SHORT
                    ).show()
                },
                negativeAction = {
                    Toast.makeText(requireContext(), getString(R.string.cancel), Toast.LENGTH_SHORT)
                        .show()


                })


        }

        binding.resettingbut.setOnClickListener {
            val customAlert = CustomAlertDialog(requireContext())
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
            customAlert.showDialog(
                message = getString(R.string.are_you_sure),
                isDarkTheme = isDarkMode,
                positiveAction = {

                   val  isLanguageReset= viewModel.resetSettings()
                    if (isLanguageReset) {
                        showRestartDialog(isDarkMode)
                    }
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.settings_reset), Toast.LENGTH_SHORT
                    ).show()

                },
                negativeAction = {
                    Toast.makeText(requireContext(), getString(R.string.cancel), Toast.LENGTH_SHORT)
                        .show()


                }
            )


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

    // Function to show a restart dialog
    private fun showRestartDialog(isDarkMode: Boolean) {
        val customAlert = CustomAlertDialog(requireContext())

        customAlert.showDialog(
            title = requireContext().getString(R.string.restart_required),
            message = requireContext().getString(R.string.the_language_has_been_updated_please_restart_the_app_for_the_changes_to_take_effect),
            isDarkTheme = isDarkMode,
            positiveAction = {
                // Restart the app
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                requireContext().startActivity(intent)
            },
            negativeAction = {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.language_will_change_later),
                    Toast.LENGTH_SHORT
                ).show()

            })


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {

            }
    }
}
