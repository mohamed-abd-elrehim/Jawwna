package com.example.jawwna.splashscreen
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.jawwna.R
import com.example.jawwna.databinding.ActivitySplashScreenBinding
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.mainactivity.MainActivity
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModel
import com.example.jawwna.mainactivity.viewmodel.MainActivityViewModelFactory
import com.example.jawwna.splashscreen.viewmodel.SplashViewModel
import com.example.jawwna.splashscreen.viewmodel.SplashViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var welcomeText: TextView
    private lateinit var viewModel: SplashViewModel

    private val splashDuration = 3000L  // 3 seconds

    // Initialize ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(
                this,
                SplashViewModelFactory(Repository.getRepository(this.application))
            ).get(
                SplashViewModel::class.java
            )
        viewModel.setUpdateLocale(Locale.getDefault().language)
        lifecycleScope.launch {
            viewModel.updateLocale.collect { language ->
                setLocale(language)
               // setFount()

            }
        }

        // Initialize View Binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lottieAnimationView = binding.lottieAnimationView
        welcomeText = binding.welcomeText

        // Get current night mode and set animation resource in ViewModel
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        viewModel.setAnimationResource(nightModeFlags)

        lifecycleScope.launch {
        // Observe the animation resource LiveData
        viewModel.animationResource.collect { animationResource ->
            // Set the animation and start it
            lottieAnimationView.setAnimation(animationResource)
            lottieAnimationView.playAnimation()
            animateTextView()
        }}



        // Start the splash timer in ViewModel
        viewModel.startSplashTimer(splashDuration)

        // Create an animation instance
        val fadeOutAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Set Animation Listener for fade-out effect
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Start MainActivity when animation ends
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Schedule fade-out animation after splash duration
        welcomeText.startAnimation(fadeOutAnimation)
    }

    private fun animateTextView() {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 2000 // 2 seconds
            fillAfter = true
        }
        welcomeText.startAnimation(fadeIn)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the animation when the activity is destroyed
        lottieAnimationView.cancelAnimation()
    }

    // Function to set the application's locale
    fun setLocale(language:String) {
        // Create a Locale object for the specified language code
        val locale = Locale(language)
        Locale.setDefault(locale) // Set the default locale for the application

        // Create a new Configuration object and set the new locale
        val config = Configuration(this.resources.configuration).apply {
            setLocale(locale) // For Android 7.0 (Nougat) and higher
        }

        // Update the resources with the new configuration
        this.resources.updateConfiguration(config, this.resources.displayMetrics)

        // For devices running Android N and higher, create a configuration context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.createConfigurationContext(config)
        }
        }
    fun setFount(){

    }




}
