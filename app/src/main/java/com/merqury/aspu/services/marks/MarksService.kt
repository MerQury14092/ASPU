package com.merqury.aspu.services.marks

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.marks.models.MarksResponse
import com.merqury.aspu.services.profile.EiosAuthorizedStringRequest
import com.merqury.aspu.services.studyplan.getStudyPlan
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.screens.marks.marksContent
import com.merqury.aspu.ui.screens.marks.marksLoadError
import com.merqury.aspu.ui.screens.marks.plan
import com.merqury.aspu.ui.navfragments.profile.profileInfo

fun getMarks() {
    val url = "http://plany.agpu.net/api/EducationalActivity/ZachBook?studentID=undefined"
    val request = EiosAuthorizedStringRequest(
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