package com.merqury.aspu.services.appconfig

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.merqury.aspu.appContext
import com.merqury.aspu.services.appconfig.models.Announcement
import com.merqury.aspu.services.appconfig.models.AnnouncementType
import com.merqury.aspu.services.appconfig.models.DatabaseConfig
import com.merqury.aspu.services.appconfig.models.UseConfig

private val remoteConfig by lazy {
    Firebase.remoteConfig.apply {
        setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        })
    }
}

private val showedAnnouncement by lazy {
    appContext!!.getSharedPreferences("announcements", Context.MODE_PRIVATE)!!
}


class AppConfig {
    companion object {
        private var databaseConfig: DatabaseConfig? = null

        var internetAccess by mutableStateOf(false)

        fun setDatabaseConfig(config: DatabaseConfig) {
            databaseConfig = config
        }

        fun saveAnnouncement(id: Long) {
            showedAnnouncement.edit().putBoolean(id.toString(), true).apply()
        }

        fun getConfig(): FirebaseRemoteConfig {
            return remoteConfig
        }

        fun getObjectMapper(): ObjectMapper {
            return mapper
        }

        fun getDeveloperExamProfileId(): Long {
            return remoteConfig.getLong("developer_exam_profile_id")
        }

        private fun getUseConfig(key: String): UseConfig {
            val string = remoteConfig.getString(key)
            try {
                mapper.readValue<List<UseConfig>>(string)
                    .forEach {
                        if (versionCheck(it.versions))
                            return it
                    }
            } catch (ignored: MismatchedInputException) {
            }
            return UseConfig(0, "all", true, null)
        }

        fun useNewsConfig(): UseConfig {
            return getUseConfig("can_use_news")
        }

        fun useTimetableConfig(): UseConfig {
            return getUseConfig("can_use_timetable")
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

        fun getDatabaseConfig(): DatabaseConfig {
            return databaseConfig!!
        }

        fun getAnnouncements(): List<Announcement> {
            try {
                return mapper.readValue<List<Announcement>>(remoteConfig.getString("announcements"))
                    .filter {
                        versionCheck(it.versions)
                    }
                    .filter {
                        it.type != AnnouncementType.simple ||
                                (it.type == AnnouncementType.simple && !showedAnnouncement.contains(
                                    it.id.toString()
                                ))
                    }.plus(internalAnnouncements)
            } catch (ignored: MismatchedInputException) {
            }
            return listOf()
        }
    }
}

private val internalAnnouncements = arrayListOf<Announcement>()

fun addInternalAnnouncement(announcement: Announcement) {
    internalAnnouncements.add(announcement)
}

private fun versionCheck(versions: String): Boolean {
    val appVersion = appContext!!
        .packageManager
        .getPackageInfo(
            appContext!!.packageName,
            0
        ).versionName!!
    try {
        versions.split(",")
            .map { it.trim { char -> char.isWhitespace() } }
            .toList().forEach { version ->
                if (version.lowercase() == "all")
                    return true

                val actual = appVersion
                    .replace(Regex("[a-zA-Z,\\- ]+"), "")
                    .split(".")
                    .map { it.toInt() }
                    .toList()

                val target = version
                    .replace(Regex("[<>a-zA-Z,\\- ]+"), "")
                    .split(".")
                    .map { it.toInt() }
                    .toList()

                (0..<minOf(actual.size, target.size)).forEach {
                    if (version[0] == '>') {
                        if(actual[it] > target[it])
                            return true
                        if(actual[it] < target[it])
                            return false
                    }
                    if (version[0] == '<'){
                        if(actual[it] < target[it])
                            return true
                        if(actual[it] > target[it])
                            return false
                    }
                    if(version[0] !in arrayOf('<', '>') && actual[it] != target[it])
                        return false
                }
                return true
            }
        return false
    } catch (e: ArrayIndexOutOfBoundsException) {
        return false
    } catch (e: NumberFormatException) {
        return false
    }
}