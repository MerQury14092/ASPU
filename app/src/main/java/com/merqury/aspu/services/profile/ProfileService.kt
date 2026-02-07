package com.merqury.aspu.services.profile

import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.profile.models.ProfileInfo
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.printlog

fun getProfileInfo(
    token: String,
    id: Int,
    onClosure: () -> Unit = {},
    onResult: (info: ProfileInfo) -> Unit
){
    val url = "http://eios.agpu.net/api/UserInfo/Student?studentID=$id"
    printlog("loading")
    val request = object: StringRequest(
        Method.GET,
        url,
        {
            async {
                try {
                    onResult(ProfileInfo.fromJson(it))
                } catch (e: Exception) {
                    printlog("error: $e");
                }
            }
        },
        {
            printlog("err")
            reauthorization(onClosure)
        }
    ){
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $token"
            return headers
        }
    }
    requestQueue!!.add(request)
}