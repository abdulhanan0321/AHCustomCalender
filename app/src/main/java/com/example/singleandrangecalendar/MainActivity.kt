package com.example.singleandrangecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.example.abdulcustomcalendar.AHCustomCalendar
import com.example.abdulcustomcalendar.AHCustomCalendarView
import com.example.abdulcustomcalendar.DateSelectedListener
import com.example.abdulcustomcalendar.model.EventModel
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var eventList: MutableList<EventModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frameLayout: FrameLayout = findViewById(R.id.frameLay)
//        val customCalendar: AHCustomCalendarView = findViewById(R.id.customCalendar)

        val dialogBtn: Button = findViewById(R.id.dialogBtn)

        val customCalendar = AHCustomCalendar(this)
        customCalendar.setMonthFontFamily(com.example.abdulcustomcalendar.R.font.poppins_semibold)
        customCalendar.setDatesFontFamily(com.example.abdulcustomcalendar.R.font.poppins_regular)
        customCalendar.setDaysFontFamily(com.example.abdulcustomcalendar.R.font.poppins_semibold)
        customCalendar.setSelectedBubbleColor("#000000")
        customCalendar.setIsMultiSelect(true)
        customCalendar.setShapeType("round")
//        customCalendar.setDateCallBackListener(object : DateSelectedListener {
//            override fun onDateSelected(startDate: Date, endDate: Date?, isMultiSelect: Boolean) {
//                when(isMultiSelect){
//                    true -> Toast.makeText(this@MainActivity, "$startDate \n$endDate",Toast.LENGTH_SHORT).show()
//                    false -> Toast.makeText(this@MainActivity, "$startDate",Toast.LENGTH_SHORT).show()
//                }
//            }
//        })

        eventList = mutableListOf()
        for (i in 0 until 3) {
            val model = EventModel()
            model.eventName = "birthday"

            when(i){
                0 -> {
                    model.eventImage = R.drawable.ic_launcher_background
                    model.eventDate = Calendar.getInstance(Locale.ENGLISH).time
                }
                1 -> {
                    model.eventImage = R.drawable.cycling_icon

                    val cal = Calendar.getInstance(Locale.ENGLISH)
                    cal.add(Calendar.DAY_OF_MONTH, 5)
                    model.eventDate = cal.time
                }
                2 -> {
                    model.eventImage = R.drawable.cycling_icon

                    val cal = Calendar.getInstance(Locale.ENGLISH)
                    cal.add(Calendar.DAY_OF_MONTH, 10)
                    model.eventDate = cal.time
                }
            }

            eventList.add(model)
        }
        customCalendar.setEventList(eventList as ArrayList<EventModel>)

        customCalendar.showCalendar(frameLayout, object : DateSelectedListener{
            override fun onDateSelected(startDate: Date, endDate: Date?, isMultiSelect: Boolean) {
                when(isMultiSelect){
                    true -> Toast.makeText(this@MainActivity, "$startDate \n$endDate",Toast.LENGTH_SHORT).show()
                    false -> Toast.makeText(this@MainActivity, "$startDate",Toast.LENGTH_SHORT).show()
                }
            }
        })

//        dialogBtn.setOnClickListener {
//            customCalendar.showCalendarDialog(object : DateSelectedListener {
//                override fun onDateSelected(startDate: Date, endDate: Date?, isMultiSelect: Boolean) {
//                    when(isMultiSelect){
//                        true -> Toast.makeText(this@MainActivity, "$startDate \n$endDate",Toast.LENGTH_SHORT).show()
//                        false -> Toast.makeText(this@MainActivity, "$startDate",Toast.LENGTH_SHORT).show()
//                    }
//                }
//            })
//        }


    }
}