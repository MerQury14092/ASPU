package com.merqury.aspu.services.studyplan

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

fun getStudyPlan(
    planId: Int,
    onError: () -> Unit,
    onSuccess: (ArrayList<PlanElement>) -> Unit
) {
    requestQueue!!.add(StringRequest(
        Request.Method.GET,
        "http://eios.agpu.net/Plans/Plan.aspx?id=$planId",
        {
            onSuccess(parseStudyPlan(it))
        },
        {
            onError()
        }
    ))
}

fun getStudyPlanIdByGroup(group: String) {

}

private fun getStudyPlanIdByPlanName(planName: String, callback: (Int?) -> Unit) {
    requestQueue!!.add(object : StringRequest(
        Method.POST,
        "http://eios.agpu.net/Plans/",
        {
            val regex = Regex("Plan\\.aspx\\?id=\\d*")
            val result = regex.find(it)
            callback(result?.value!!.split("=")[1].toInt())
        },
        {

        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            return hashMapOf(
                "Cookie" to ConstantsForEiosSite.cookie
            )
        }
        override fun getBodyContentType(): String {
            return "application/x-www-form-urlencoded; charset=utf-8"
        }
        override fun getBody(): ByteArray {
            return ConstantsForEiosSite.getBodyForStudyIdByPlanNameRequest(planName).toByteArray(StandardCharsets.UTF_8);
        }
    })
}

fun getStudyPlanNameByGroup(group: String, callback: (String?) -> Unit) {
    requestQueue!!.add(object : StringRequest(
        Method.POST,
        "http://eios.agpu.net/Totals/Default.aspx",
        {
            val regex = Regex("[0-9\\-A-Za-zа-яА-Я]*\\.plx")
            val result = regex.find(it)
            callback(result?.value)
        },
        {}
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            return hashMapOf(
                "Cookie" to ConstantsForEiosSite.cookie
            )
        }
        override fun getBodyContentType(): String {
            return "application/x-www-form-urlencoded; charset=utf-8"
        }
        override fun getBody(): ByteArray {
            return ConstantsForEiosSite.getBodyForStudyPlansByGroupRequest(group).toByteArray(StandardCharsets.UTF_8);
        }
    })
}

private fun parseStudyPlan(html: String): ArrayList<PlanElement> {
    val planElements = ArrayList<PlanElement>()

    val document: Document = Jsoup.parse(html)

    var blockName = ""

    val rows =
        ArrayList(document.select("tr.dxgvGroupRow_MaterialCompact,tr.dxgvDataRow_MaterialCompact")
            .stream()
            .map {
                if (it.hasClass("dxgvDataRow_MaterialCompact"))
                    it.append("<td>$blockName</td>")
                else if (it.hasClass("dxgvGroupRow_MaterialCompact")) {
                    blockName = it.select("td").toList()[1].text().split(":")[1].trim()
                }
                it
            }
            .filter {
                it.hasClass("dxgvDataRow_MaterialCompact")
            }
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
        try {
            if (row.size < 6) continue
            val planElement = PlanElement()
            planElement.name = row[1].text()
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
            planElement.blockName = row[row.size - 1].text()
            planElement.hours = (planElement.labCount + planElement.lecCount + planElement.pracCount) * 2
            planElements.add(planElement)
        } catch (ignored: IndexOutOfBoundsException) {}
    }
    return planElements
}