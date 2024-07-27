package com.merqury.aspu.services.exam

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.models.ExamMemberModel
import com.merqury.aspu.ui.async
import org.jsoup.Jsoup

fun getExamMembers(
    courseId: Int,
    onError: (String) -> Unit,
    onSuccess: (List<ExamMemberModel>) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/user/index.php?id=$courseId&perpage=5000",
        { html ->
            async { onSuccess(
                Jsoup.parse(html)
                    .select(".generaltable tbody tr[id^=user][class='']")
                    .map { tr ->
                        val name = if (tr.select(".userinitials").isNotEmpty())
                            tr.select(".userinitials")
                                .attr("title")
                                .split(" ")
                        else
                            tr.select("th a")
                                .text()
                                .split(" ")
                        ExamMemberModel(
                            tr.select("label[for^=user]")
                                .attr("for")
                                .replace("user", "")
                                .toLong(),
                            name[1].lowercase().replaceFirstChar { it.uppercase() },
                            name[0].lowercase().replaceFirstChar { it.uppercase() },
                            if (tr.select("img").isNotEmpty())
                                tr.select("img").attr("src").replace("f2","f1")
                            else null
                        )
                    }.toList()
            ) }
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}