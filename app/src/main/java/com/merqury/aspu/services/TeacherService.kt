package com.merqury.aspu.services

import android.util.Log
import com.merqury.aspu.ui.printlog

fun String.fetchFullFio(){
    if(cache.contains("fio $this")) {
        return
    }
    val mutated = replace(".", ". ").trim()
    val fio = mutated.split(" ")
    if(fio.size != 3)
        return
    val initialsRegex = Regex("[А-Я].")
    if(!initialsRegex.matches(fio[1]) || !initialsRegex.matches(fio[2]))
        return
    val lastnameRegex = Regex("[А-Яа-я]*")
    if(!lastnameRegex.matches(fio[0]))
        return

    executeSqlQuery(
        """
            SELECT * FROM teachers WHERE last_name='${fio[0]}'
        """.trimIndent(),
        {
            while (it.next()){
                val lname = it.getString("last_name")
                val fname = it.getString("first_name")
                val tname = it.getString("father_name")
                if("${fname[0]}." == fio[1] && "${tname[0]}." == fio[2]) {
                    cache.edit().putString("fio $this", "$lname $fname $tname").apply()
                }
            }
        },
        {
            Log.e("database-sql-error", it.toString())
            printlog(it.toString())
        }
    )
}

fun String.toInitials(): String {
    val fio = split(" ")
    return "${fio[0]} ${fio[1][0]}.${fio[2][0]}."
}