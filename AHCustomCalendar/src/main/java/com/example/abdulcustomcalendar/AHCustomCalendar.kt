package com.example.abdulcustomcalendar

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.abdulcustomcalendar.adapter.CalendarAdapter
import com.example.abdulcustomcalendar.adapter.DaysAdapter
import com.example.abdulcustomcalendar.model.CalenderModel
import com.example.abdulcustomcalendar.model.EventModel
import com.example.abdulcustomcalendar.util.ImportantFunction
import java.text.SimpleDateFormat
import java.util.*


class AHCustomCalendar(private val context: Context) {

    private val dates = ArrayList<CalenderModel>()
    private lateinit var recyclerView: RecyclerView

    private var shapeType = "rectangle"
    private var isMultiSelect = false
    private var selectedBubbleColor = "#163A64"
    private var fontFamilyDates = R.font.poppins_semibold
    private var fontFamilyMonth = R.font.poppins_semibold
    private var fontFamilyDays = R.font.poppins_semibold

    private var dialogFontOptionFamilyButton = R.font.poppins_semibold
    private var dialogPositiveButtonText = "Done"
    private var dialogNegativeButtonText = "Cancel"
    private var dialogOptionButtonTextColor = "#000000"

    private var eventList: MutableList<EventModel> = mutableListOf()
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)

    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]

    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

//    interface DateSelectedListener{
//        fun onDateSelected(startDate: Date, endDate: Date?, isMultiSelect:Boolean)
//    }

    companion object{
        var selectedDate: Date? = null
        var endDate: Date? = null
    }

    fun setMonthFontFamily(fontFamilyMonth: Int){
        this.fontFamilyMonth = fontFamilyMonth
    }

    fun setDaysFontFamily(fontFamilyDays: Int){
        this.fontFamilyDays = fontFamilyDays
    }

    fun setDatesFontFamily(fontFamilyDates: Int){
        this.fontFamilyDates = fontFamilyDates
    }

    fun setDialogOptionButtonFontFamily(optionButtonFontFamily: Int){
        this.dialogFontOptionFamilyButton = optionButtonFontFamily
    }

    fun setDialogPositiveButtonText(dialogPositiveButtonText: String){
        this.dialogPositiveButtonText = dialogPositiveButtonText
    }

    fun setDialogNegativeButtonText(dialogOptionNegativeButtonText: String){
        this.dialogNegativeButtonText = dialogOptionNegativeButtonText
    }

    fun setDialogOptionButtonTextColor(dialogOptionButtonTextColor: String){
        this.dialogOptionButtonTextColor = dialogOptionButtonTextColor
    }

    fun setShapeType(shapeType: String){
        this.shapeType = shapeType
    }

    fun setIsMultiSelect(isMultiSelect: Boolean){
        this.isMultiSelect = isMultiSelect
    }

    fun setSelectedBubbleColor(bubbleColor: String){
        this.selectedBubbleColor = bubbleColor
    }

    fun setEventList(eventList: ArrayList<EventModel>){
        this.eventList = eventList
    }

    /**
     * show alert dialog Calendar
     * @param dateSelectedListener call back listener for date selection
     * */
    fun showCalendarDialog(dateSelectedListener: DateSelectedListener){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.main)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)

        val cardView: CardView = dialog.findViewById(R.id.cardView)
        val cardViewMarginParams = cardView.layoutParams as MarginLayoutParams
        cardViewMarginParams.setMargins(40, 20, 40, 20)
        cardView.requestLayout()

        val txtCurrentMonth: TextView = dialog.findViewById(R.id.txt_current_month)
        txtCurrentMonth.typeface = ResourcesCompat.getFont(context, fontFamilyMonth)

        val calendarPrevButton: ImageView = dialog.findViewById(R.id.calendar_prev_button)
        val calendarNextButton: ImageView = dialog.findViewById(R.id.calendar_next_button)
        val dialogBtnLay: LinearLayout = dialog.findViewById(R.id.dialogBtnLay)

        dialogBtnLay.addView(addText(dialog, dialogNegativeButtonText, false, dateSelectedListener))
        dialogBtnLay.addView(addText(dialog, dialogPositiveButtonText, true, dateSelectedListener))

        selectedDate = Calendar.getInstance().time

        val daysNameRecycler: RecyclerView = dialog.findViewById(R.id.daysNameRecycler)

        daysNameRecycler.adapter = getDaysAdapter(daysNameRecycler)


        recyclerView = dialog.findViewById(R.id.calendar_recycler_view)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        setUpCalendar(null, context, txtCurrentMonth, dateSelectedListener)

        calendarPrevButton.setOnClickListener {
            if (cal.after(currentDate)) {
                cal.add(Calendar.MONTH, -1)
                if (cal == currentDate)
                    setUpCalendar(null, context, txtCurrentMonth, dateSelectedListener)
                else
                    setUpCalendar(changeMonth = cal, context, txtCurrentMonth, dateSelectedListener)
            }
        }

        calendarNextButton.setOnClickListener {
//            if (cal.before(lastDayInCalendar)) {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar(changeMonth = cal, context, txtCurrentMonth, dateSelectedListener)
//            }
        }

        dialog.show()

    }

    /**
     * show dialog in a frameLayout
     * @param frameLayout to show calendar view in frame
     * @param dateSelectedListener call back listener for date selection
     * */
    fun showCalendar(frameLayout: FrameLayout, dateSelectedListener: DateSelectedListener){

        val factory = LayoutInflater.from(context)
        val view: View = factory.inflate(R.layout.main, null)
        frameLayout.addView(view)

        val cardView: CardView = view.findViewById(R.id.cardView)
        cardView.radius = 0f
        val txtCurrentMonth: TextView = view.findViewById(R.id.txt_current_month)
        txtCurrentMonth.typeface = ResourcesCompat.getFont(context, fontFamilyMonth)

        val calendarPrevButton: ImageView = view.findViewById(R.id.calendar_prev_button)
        val calendarNextButton: ImageView = view.findViewById(R.id.calendar_next_button)

        selectedDate = Calendar.getInstance().time

        val daysNameRecycler: RecyclerView = view.findViewById(R.id.daysNameRecycler)

        daysNameRecycler.adapter = getDaysAdapter(daysNameRecycler)


        recyclerView = view.findViewById(R.id.calendar_recycler_view)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        setUpCalendar(null, context, txtCurrentMonth, dateSelectedListener)

        calendarPrevButton.setOnClickListener {
            if (cal.after(currentDate)) {
                cal.add(Calendar.MONTH, -1)
                if (cal == currentDate)
                    setUpCalendar(null, context, txtCurrentMonth, dateSelectedListener)
                else
                    setUpCalendar(changeMonth = cal, context, txtCurrentMonth, dateSelectedListener)
            }
        }

        calendarNextButton.setOnClickListener {
//            if (cal.before(lastDayInCalendar)) {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar(changeMonth = cal, context, txtCurrentMonth, dateSelectedListener)
//            }
        }

    }


    /**
     * function to set Calendar days Adapter
     * @param daysNameRecycler recycler view for the days
     * */
    private fun getDaysAdapter(daysNameRecycler: RecyclerView): DaysAdapter {
        daysNameRecycler.layoutManager = GridLayoutManager(context, 7)
        val days = mutableListOf<String>()
        days.add("Sun")
        days.add("Mon")
        days.add("Tue")
        days.add("Wed")
        days.add("Thu")
        days.add("Fri")
        days.add("Sat")

        return DaysAdapter(context, days, fontFamilyDays)
    }

    /**
     * function to set & get calendar date values and set in RecyclerView
     * @param changeMonth calendar month to which we are getting value
     * @param context view context to class
     * @param txt_current_month textView of current month shown on top of view
     * @param dateSelectedListener call back listener for date selection
     * */
    private fun setUpCalendar(changeMonth: Calendar? = null,
                              context: Context,
                              txt_current_month: TextView,
                              dateSelectedListener: DateSelectedListener){
        // first part
        txt_current_month.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }

        // second part
        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)

        val calenderModel = CalenderModel()
        calenderModel.date = monthCalendar.time
        calenderModel.isForceEmpty = true
        val dayOfTheWeek = sdf.format(monthCalendar.time) as String // Thursday
        var count = 0
        if (dayOfTheWeek.equals("Mon", true)){
            count = 1
        }else if (dayOfTheWeek.equals("Tue", true)){
            count = 2
        }else if (dayOfTheWeek.equals("Wed", true)){
            count = 3
        }else if (dayOfTheWeek.equals("Thu", true)){
            count = 4
        }else if (dayOfTheWeek.equals("Fri", true)){
            count = 5
        }else if (dayOfTheWeek.equals("Sat", true)){
            count = 6
        }

        currentPosition = count
        for (i in 0 until count){
            dates.add(calenderModel)
        }

        while (dates.size < maxDaysInMonth + count) {
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size

            val calenderModels = CalenderModel()
            calenderModels.date = monthCalendar.time
            calenderModels.isForceEmpty = false

            /** for comparing events dates with month dates*/
            eventList.forEach {
                if (ImportantFunction.getDateOnlyFromFullDate(it.eventDate!!)
                    == ImportantFunction.getDateOnlyFromFullDate(calenderModels.date!!))
                    calenderModels.eventList.add(it)
            }

            dates.add(calenderModels)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // third part
        val layoutManager = GridLayoutManager(context, 7)
        recyclerView.layoutManager = layoutManager
        val calendarAdapter = CalendarAdapter(context, dates, currentDate,
            isMultiSelect, shapeType, fontFamilyDates, selectedBubbleColor)
        recyclerView.adapter = calendarAdapter

        when {
            currentPosition > 2 -> recyclerView.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> recyclerView.scrollToPosition(currentPosition)
            else -> recyclerView.scrollToPosition(currentPosition)
        }

        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, isMultiSelect: Boolean) {
                val clickCalendar = Calendar.getInstance()


                when(isMultiSelect){
                    true -> {
                        if (selectedDate == null && endDate == null){
                            selectedDate = dates[position].date!!
                        }else if (selectedDate != null && endDate == null){
                            endDate = dates[position].date!!

                            if(selectedDate!!.after(endDate)){
                                endDate = selectedDate.also { selectedDate = endDate }
                            }
                        }else if(selectedDate != null && endDate != null){
                            selectedDate= dates[position].date!!
                            endDate = null
                        }

                    }
                    false -> {
                        selectedDate = dates[position].date!!
                        endDate = null
                    }
                }

                Log.d("asfvafv", "$selectedDate $endDate")

                clickCalendar.time = selectedDate!!
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]

                calendarAdapter.updateList(shapeType)

                dateSelectedListener.onDateSelected(selectedDate!!, endDate, isMultiSelect)


            }
        })
    }

    /**
     * function to add option text to dialog calendar
     * @param dialog view for dialog on which new are setting text
     * @param buttonValue set text value to option button
     * @param isPositive boolean check for negative and positive button
     * @param dateSelectedListener call back listener for date selection
     * */
    private fun addText(dialog: Dialog, buttonValue: String,
                        isPositive: Boolean, dateSelectedListener: DateSelectedListener): TextView{

        val myText = TextView(context)
        myText.text = buttonValue
        myText.typeface = ResourcesCompat.getFont(context, dialogFontOptionFamilyButton)
        myText.setTextColor(ColorStateList.valueOf(Color.parseColor(dialogOptionButtonTextColor)))
        myText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        myText.setPadding(30, 0, 0, 0)

        if (isPositive){
            myText.setOnClickListener {
                dateSelectedListener.onDateSelected(selectedDate!!, endDate, isMultiSelect)
                dialog.dismiss()
            }
        }else {
            myText.setOnClickListener {
                dialog.dismiss()
            }
        }

        return myText

    }

}