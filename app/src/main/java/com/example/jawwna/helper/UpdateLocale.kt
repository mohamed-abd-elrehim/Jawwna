package com.example.jawwna.helper

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.jawwna.R
import com.example.jawwna.customui.CustomAlertDialog
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.IPreferencesSettingsHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.PreferencesSettingsHelper
import com.example.jawwna.mainactivity.MainActivity
import java.util.*

class UpdateLocale private constructor(private val application: Context) : IUpdateLocale {
 private var isDarkMode=false

    companion object {
        @Volatile
        private var INSTANCE: UpdateLocale? = null

        // Singleton pattern to get a single instance of UpdateLocale
        fun getInstance(application: Application): UpdateLocale {
            return INSTANCE ?: synchronized(this) {
                UpdateLocale(application).also {
                    INSTANCE = it // Set the instance to the newly created UpdateLocale object
                }
            }
        }

    }

    // Function to set the application's locale
    override fun setLocale(language:String) {
        // Create a Locale object for the specified language code
        val locale = Locale(language)
        Locale.setDefault(locale) // Set the default locale for the application

        // Create a new Configuration object and set the new locale
        val config = Configuration(application.resources.configuration).apply {
            setLocale(locale) // For Android 7.0 (Nougat) and higher
        }

        // Update the resources with the new configuration
        application.resources.updateConfiguration(config, application.resources.displayMetrics)

        // For devices running Android N and higher, create a configuration context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            application.createConfigurationContext(config)
        }

        // Get current night mode
        val nightModeFlags =
            application.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            isDarkMode=true
        }else{
            isDarkMode=false

        }

  /*      // Show a dialog informing the user to restart the app
        showRestartDialog(isDarkMode)*/
    }
//    // Function to show a restart dialog
//    private fun showRestartDialog(isDarkMode: Boolean) {
//        val customAlert = CustomAlertDialog(application)
//
//        customAlert.showDialog(
//            title = application.getString(R.string.restart_required),
//            message = application.getString(R.string.the_language_has_been_updated_please_restart_the_app_for_the_changes_to_take_effect),
//            isDarkTheme = isDarkMode,
//            positiveAction = {
//                // Restart the app
//                val intent = Intent(application, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                application.startActivity(intent)
//            },
//            negativeAction = {
//                Toast.makeText(application,
//                    application.getString(R.string.language_will_change_later), Toast.LENGTH_SHORT).show()
//
//            })
//
//
//
//    }


}
