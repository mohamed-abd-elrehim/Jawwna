package com.example.jawwna.splashscreen
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.example.jawwna.R
import com.example.jawwna.databinding.ActivitySplashScreenBinding
import com.example.jawwna.homescreen.MainActivity
import com.example.jawwna.splashscreen.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var welcomeText: TextView

    private val splashDuration = 3000L  // 3 seconds

    // Initialize ViewModel
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lottieAnimationView = binding.lottieAnimationView
        welcomeText = binding.welcomeText

        // Get current night mode and set animation resource in ViewModel
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        viewModel.setAnimationResource(nightModeFlags)

        // Observe the animation resource LiveData
        viewModel.animationResource.observe(this, Observer { animationResource ->
            // Set the animation and start it
            lottieAnimationView.setAnimation(animationResource)
            lottieAnimationView.playAnimation()
            animateTextView()
        })

        // Observe navigation event to transition to MainActivity
        viewModel.navigateToMainActivity.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()  // Close splash screen
            }
        })

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
}
