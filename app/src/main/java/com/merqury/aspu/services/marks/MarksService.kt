package com.merqury.aspu.services.marks

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.marks.models.MarksResponse
import com.merqury.aspu.services.profile.AuthorizedStringRequest
import com.merqury.aspu.services.studyplan.getStudyPlan
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.marks.marksContent
import com.merqury.aspu.ui.navfragments.marks.marksLoadError
import com.merqury.aspu.ui.navfragments.marks.plan
import com.merqury.aspu.ui.navfragments.profile.profileInfo

fun getMarks() {
    val url = "http://plany.agpu.net/api/EducationalActivity/ZachBook?studentID=undefined"
    val request = AuthorizedStringRequest(
        Method.GET,
        url,
        { marks ->
            async {
                getStudyPlan(
                    profileInfo!!.data!!.plan!!.item2!!.toInt(), {
                        marksLoadError = "Ошибка!"
                    }
                ) {
                    plan = it
                    marksContent = MarksResponse.fromJson(marks)
                }

            }
        },
        {
            marksLoadError = "Ошибка!"
        }
    )
    requestQueue!!.add(request)
}