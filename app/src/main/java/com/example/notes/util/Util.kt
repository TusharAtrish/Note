package com.example.notes.util

import java.util.*

object Util  {
    fun getFormattedTime(time : Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val month = calendar[Calendar.MONTH] +1
        val date = calendar[Calendar.DAY_OF_MONTH]
        val year = calendar[Calendar.YEAR]
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        return "$date/$month/$year $hour:$minute"
    }
}