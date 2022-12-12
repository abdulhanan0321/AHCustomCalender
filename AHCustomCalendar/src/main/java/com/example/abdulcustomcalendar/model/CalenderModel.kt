package com.example.abdulcustomcalendar.model

import java.util.*
import kotlin.collections.ArrayList

class CalenderModel {

    var date: Date? = null
    var isForceEmpty: Boolean = false
    var eventList: ArrayList<EventModel> = ArrayList()
}