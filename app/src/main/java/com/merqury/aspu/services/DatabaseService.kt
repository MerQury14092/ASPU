package com.merqury.aspu.services

import android.util.Log
import com.merqury.aspu.apiDomain
import com.merqury.aspu.ui.async
import org.postgresql.util.PSQLException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

private fun getDbConnection(): Connection{
//    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//    StrictMode.setThreadPolicy(policy)
    Class.forName("org.postgresql.Driver")
    val url = "jdbc:postgresql://$apiDomain:3333/agpu_db"
    val username = "guest"
    val password = "lapshin"
    return DriverManager.getConnection(url, username, password)
}

fun executeSqlQuery(sql: String, onSuccess: (resultSet: ResultSet) -> Unit, onError: (e: PSQLException) -> Unit) {
    async {
        try {
            val connection = getDbConnection()
            val statement = connection.createStatement()
            val result = statement.executeQuery(sql)
            onSuccess(result)
            statement.close()
            connection.close()
        } catch (e: PSQLException){
            onError(e)
        } catch (e: Exception){
            Log.e("unknown-exception", e.toString())
        }
    }
}