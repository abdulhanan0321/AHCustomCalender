package com.example.abdulcustomcalendar

import java.util.*

interface DateSelectedListener{
    fun onDateSelected(startDate: Date, endDate: Date?, isMultiSelect:Boolean)
}