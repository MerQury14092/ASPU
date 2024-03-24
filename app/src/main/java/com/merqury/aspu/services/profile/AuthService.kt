package com.merqury.aspu.services.profile

import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.navfragments.profile.secretPreferences
import com.merqury.aspu.ui.navfragments.profile.showEiosAuthModalWindow
import org.json.JSONObject

fun reauthorization(onClosure: () -> Unit){
    if(
        !secretPreferences.contains("login") ||
        !secretPreferences.contains("password")
    )
        showEiosAuthModalWindow {}
    else {
        getAuthToken(
            secretPreferences.getString("login", null)!!,
            secretPreferences.getString("password", null)!!,
            { token, id ->
                secretPreferences
                    .edit()
                    .putString("authToken", token)
                    .putInt("userId", id)
                    .apply()
                onClosure()
            },
            {
                showEiosAuthModalWindow(msg = "Bad credentials")
            }
        )
    }
}

fun getAuthToken(
    login: String,
    pass: String,
    onSuccess: (token: String, id: Int) -> Unit,
    onError: (msg: String) -> Unit
) {
    val url = "http://plany.agpu.net/api/tokenauth"
    val request = object : StringRequest(
        Method.POST,
        url,
        {
            val response = JSONObject(it)
            if (response.getInt("state") == 1) {
                val id = response
                    .getJSONObject("data")
                    .getJSONObject("data")
                    .getInt("id")
                val accessToken = response
                    .getJSONObject("data")
                    .getJSONObject("data")
                    .getString("accessToken")
                onSuccess(accessToken, id)
            }
            else
                onError(response.getString("msg"))
        },
        {
            onError(it.toString())
        }
    ) {
        override fun getBody(): ByteArray {
            return "{\"userName\":\"$login\",\"password\":\"$pass\"}".toByteArray()
        }

        override fun getBodyContentType(): String {
            return "application/json"
        }
    }
    requestQueue!!.add(request)
}