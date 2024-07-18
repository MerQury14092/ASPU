package com.merqury.aspu.services.appconfig

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.merqury.aspu.appContext
import com.merqury.aspu.services.appconfig.models.Announcement
import com.merqury.aspu.services.appconfig.models.UseConfig

private val remoteConfig by lazy {
    Firebase.remoteConfig.apply {
        setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        })
    }
}


class AppConfig {
    companion object {
        fun getConfig(): FirebaseRemoteConfig {
            return remoteConfig
        }

        fun getDeveloperExamProfileId(): Long {
            return remoteConfig.getLong("developer_exam_profile_id")
        }

        private fun getUseConfig(key: String): UseConfig{
            val string = remoteConfig.getString(key)
            try {
                mapper.readValue<List<UseConfig>>(string)
                    .forEach {
                        if(versionCheck(it.versions))
                            return it
                    }
            } catch (ignored: MismatchedInputException){}
            return UseConfig(0, "all", true, null)
        }

        fun useNewsPageConfig(): UseConfig {
            return getUseConfig("can_use_news_page")
        }

        fun useTimetablePageConfig(): UseConfig {
            return getUseConfig("can_use_timetable_page")
        }

        fun useEiosConfig(): UseConfig {
            return getUseConfig("can_use_eios")
        }

        fun useEiosMessengerConfig(): UseConfig {
            return getUseConfig("can_use_eios_messenger")
        }

        fun useExamConfig(): UseConfig {
            return getUseConfig("can_use_exam")
        }

        fun getApiDomain(): String {
            return remoteConfig.getString("api_domain")
        }

        fun getAnnouncements(): List<Announcement> {
            try {
                return mapper.readValue<List<Announcement>>(remoteConfig.getString("announcements"))
                    .filter { versionCheck(it.versions) }
            } catch (ignored: MismatchedInputException){}
            return listOf()
        }
    }
}

private fun versionCheck(versions: String): Boolean {
    val appVersion = appContext!!
        .packageManager
        .getPackageInfo(
            appContext!!.packageName,
            0
        ).versionName!!
        .replace(Regex("[^0-9.]"), "")
        .toDouble()
    try {
        versions.split(",")
            .map { it.trim { char -> char.isWhitespace() } }
            .toList().forEach { version ->
                if(version.lowercase() == "all")
                    return true
                val versionInDouble = version
                    .replace(Regex("[^0-9<>.]"), "")
                    .toDouble()
                return when (version[0]) {
                    '<' -> appVersion < versionInDouble
                    '>' -> appVersion > versionInDouble
                    else -> appVersion == versionInDouble
                }
            }
        return false
    } catch (e: ArrayIndexOutOfBoundsException) {
        return false
    } catch (e: NumberFormatException) {
        return false
    }
}