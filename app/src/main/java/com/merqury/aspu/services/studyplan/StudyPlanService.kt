package com.merqury.aspu.services.studyplan

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.stream.Collectors

fun getStudyPlan(
    planId: Int,
    onError: () -> Unit,
    onSuccess: (ArrayList<PlanElement>) -> Unit
) {
    requestQueue!!.add(StringRequest(
        Request.Method.GET,
        "http://plany.agpu.net/Plans/Plan.aspx?id=$planId",
        {
            onSuccess(parseStudyPlan(it))
        },
        {
            onError()
        }
    ))
}

private fun parseStudyPlan(html: String): ArrayList<PlanElement> {
    val planElements = ArrayList<PlanElement>()

    val document: Document = Jsoup.parse(html)

    val rows = ArrayList(document.select("tr.dxgvDataRow_MaterialCompact")
        .stream()
        .filter { row -> row.classNames().size == 1 }
        .map { row -> ArrayList(row.select("td").stream().collect(Collectors.toList())) }
        .collect(Collectors.toList()))

    for (i in rows.indices) {
        for (j in 0 until rows[i].size) {
            val cell = rows[i][j]
            if (cell.hasAttr("rowspan")) {
                val rowspan: Int = cell.attr("rowspan").toInt()
                cell.attr("rowspan", 1.toString())
                for (k in 1 until rowspan) {
                    try {
                        rows[i + k].add(j, cell)
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }

    for (row in rows) {
        if (row.size < 6) continue
        val planElement = PlanElement()
        planElement.name = row[1].text()
        try {
            planElement.hours = row[3].text().toInt()
        } catch (ignored: NumberFormatException) {
            planElement.hours = 0
        }
        try {
            planElement.lecCount = row[7].text().toInt()
        } catch (ignored: NumberFormatException) {
            planElement.lecCount = 0
        }
        try {
            planElement.pracCount = row[8].text().toInt()
        } catch (ignored: NumberFormatException) {
            planElement.pracCount = 0
        }
        try {
            planElement.labCount = row[9].text().toInt()
        } catch (ignored: NumberFormatException) {
            planElement.labCount = 0
        }
        try {
            planElement.semester = row[5].text().toInt()
        } catch (ignored: NumberFormatException) {
            planElement.semester = 0
        }
        when (row[6].text()) {
            "Зач" -> planElement.control = PlanElement.ControlType.cred
            "ЗачО" -> planElement.control = PlanElement.ControlType.dif_cred
            "Экз" -> planElement.control = PlanElement.ControlType.exam
            "Экз (КР)" -> planElement.control = PlanElement.ControlType.exam_kr
            else -> planElement.control = PlanElement.ControlType.undefined
        }
        planElements.add(planElement)
    }

    return planElements
}