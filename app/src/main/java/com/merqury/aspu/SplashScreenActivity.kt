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
import com.fasterxml.jackson.module.kotlin.readValue
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.appconfig.addInternalAnnouncement
import com.merqury.aspu.services.appconfig.models.Announcement
import com.merqury.aspu.services.appconfig.models.AnnouncementType
import com.merqury.aspu.services.appconfig.models.DatabaseConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.after
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.greetings.GreetingsPager
import xyz.teamgravity.checkinternet.CheckInternet
import kotlin.time.Duration.Companion.milliseconds


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : Activity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exceptionHandler = ExceptionHandler(this)
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
        appContext = this
        appContext!!.getSharedPreferences("news-cache", Context.MODE_PRIVATE).edit().clear().apply()
        AppSettings.timeCache = 0L
        setTheme(getThemeStyle())
        AppConfig.getConfig().fetchAndActivate().addOnSuccessListener {
            Thread {
                CheckInternet().check {
                    if (it)
                        exceptionHandler.pushAllExceptions()
                }
            }.start()
            if (AppSettings.firstLaunch)
                startActivity(Intent(this, GreetingsPager::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
            this.overridePendingTransition(0, 0)
            finish()
            async {
                AppConfig.setDatabaseConfig(
                    AppConfig.getObjectMapper()
                        .readValue<DatabaseConfig>(
                            AppConfig.getConfig().getString("database_config")
                        )
                )

            }
        }
        setContentView(R.layout.spash_screen_layout)

        imageView = findViewById(R.id.image_view)

        animateImageView()

        CheckInternet().check { hasInternet ->
            AppConfig.internetAccess = hasInternet
            if(!hasInternet) {
                after(500.milliseconds) {
                    if(AppSettings.firstLaunch)
                        addInternalAnnouncement(Announcement(
                            0,
                            "all",
                            false,
                            AnnouncementType.blocking,
                            "Нет подключения к интернету!",
                            "Для первого запуска приложения необходимо подключение к интернету!"
                        ))
//                    else
//                        addInternalAnnouncement(Announcement(
//                            0,
//                            "all",
//                            false,
//                            AnnouncementType.intrusive,
//                            "Нет подключения к интернету!",
//                            "В связи с отсутствием интернета вам предоставлен " +
//                                    "ограниченный функционал приложения."
//                        ))
                    startActivity(Intent(this, MainActivity::class.java))
                    this.overridePendingTransition(0, 0)
                    finish()
                }
            }
        }
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
        return when (theme) {
            "dark" -> R.style.Theme_ASPU_dark
            "sea" -> R.style.Theme_ASPU_sea
            else -> R.style.Theme_ASPU_light
        }
    }
}