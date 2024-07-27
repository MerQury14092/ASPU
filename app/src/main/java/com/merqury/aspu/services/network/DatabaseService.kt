package com.merqury.aspu.services.network

import android.util.Log
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.ui.async
import org.postgresql.util.PSQLException
import java.sql.DriverManager
import java.sql.ResultSet

class DatabaseService {
    companion object {
        val config by lazy {
            AppConfig.getDatabaseConfig()
        }
    }
}

private val dbConnection by lazy {
    val config = DatabaseService.config
    Class.forName(config.javaDriverName)
    val url = config.jdbcUrl
    val username = config.user
    val password = config.pass
    DriverManager.getConnection(url, username, password)
}

fun executeSqlQuery(sql: String, onSuccess: (resultSet: ResultSet) -> Unit, onError: (e: PSQLException) -> Unit) {
    async {
        try {
            val connection = dbConnection
            val statement = connection.createStatement()
            val result = statement.executeQuery(sql)
            onSuccess(result)
            statement.close()
        } catch (e: PSQLException){
            onError(e)
        } catch (e: Exception){
            Log.e("unknown-exception", e.toString())
        }
    }
}