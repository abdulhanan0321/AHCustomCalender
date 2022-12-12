package com.example.abdulcustomcalendar.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ImportantFunction {

    companion object{
        fun getDateOnlyFromFullDate(date: Date): Date {
            val stringDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            var datee: Date? = null
            try {
                datee = format.parse(stringDate)
                println(datee)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return datee!!
        }

        fun dateDiff(a: Date, b: Date, d: Date): Boolean {
            return a.compareTo(d) * d.compareTo(b) > 0
        }
    }
}