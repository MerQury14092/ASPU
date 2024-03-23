package com.merqury.aspu.services.profile

import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.profile.models.ProfileInfo

fun getProfileInfo(
    token: String,
    id: Int,
    onResult: (info: ProfileInfo) -> Unit
){
    val url = "http://plany.agpu.net/api/UserInfo/Student?studentID=$id"
    val request = object: StringRequest(
        Method.GET,
        url,
        {
            onResult(ProfileInfo.fromJson(it))
        },
        {
            reauthorization()
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