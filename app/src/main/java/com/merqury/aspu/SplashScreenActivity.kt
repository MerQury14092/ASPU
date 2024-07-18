package com.merqury.aspu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.merqury.aspu.services.appconfig.AppConfig

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity: Activity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = this
        setTheme(getThemeStyle())
        AppConfig.getConfig().fetchAndActivate().addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            this.overridePendingTransition(0,0)
            finish()
        }
        setContentView(R.layout.spash_screen_layout)

        imageView = findViewById(R.id.image_view)

        animateImageView()
    }

    private fun animateImageView() {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 600
        }

        imageView.startAnimation(scaleAnimation)
    }

    private fun getThemeStyle(): Int {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)!!
        val theme = prefs.getString(
            "theme",
            if (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
            )
                "dark"
            else
                "light"
        )!!
        return when (theme){
            "dark" -> R.style.Theme_ASPU_dark
            "sea" -> R.style.Theme_ASPU_sea
            else -> R.style.Theme_ASPU_light
        }
    }
}