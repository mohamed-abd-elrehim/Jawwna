package com.example.jawwna

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.jawwna.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashDuration = 3000L  // 3 seconds

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Determine the correct animation resource based on the current night mode
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val animationResource = when (nightModeFlags) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> R.raw.nightmodesplashanim // Dark mode animation
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> R.raw.lightmodesplashanim // Light mode animation
            else -> R.raw.lightmodesplashanim // Default to light mode
        }

        lottieAnimationView = binding.lottieAnimationView
        welcomeText = binding.welcomeText

        // Set the animation resource
        lottieAnimationView.setAnimation(animationResource)

        // Start the splash screen animation
        lottieAnimationView.playAnimation()
        animateTextView()

        // Create an animation instance
        val fadeOutAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Set an Animation Listener to transition to MainActivity when the fade-out animation ends
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                // Finish SplashActivity
                finish()
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Start the MainActivity after the fade-out animation ends

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        // Schedule the fade-out animation to start after the splash screen duration
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                welcomeText.startAnimation(fadeOutAnimation)
            }
        }, splashDuration)
    }

    private fun animateTextView() {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 2000 // 1.5 seconds
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
