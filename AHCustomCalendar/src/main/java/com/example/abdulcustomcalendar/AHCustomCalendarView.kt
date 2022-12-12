package com.example.abdulcustomcalendar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Px
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


class AHCustomCalendarView: LinearLayout {

    private val dates = ArrayList<CalenderModel>()
    private var days = mutableListOf<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private var dateSelectedListener: DateSelectedListener? = null
    private lateinit var txtCurrentMonth: TextView
    private lateinit var daysNameRecycler: RecyclerView

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



    constructor(context: Context?) : super(context){
        initializeCalendar()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initializeCalendar()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initializeCalendar()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun initializeCalendar() {
        val inflate = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflate.inflate(R.layout.main, this, true)

        val cardView: CardView = view.findViewById(R.id.cardView)
        cardView.radius = 0f
        txtCurrentMonth = view.findViewById(R.id.txt_current_month)
        txtCurrentMonth.typeface = ResourcesCompat.getFont(context, fontFamilyMonth)

        val calendarPrevButton: ImageView = view.findViewById(R.id.calendar_prev_button)
        val calendarNextButton: ImageView = view.findViewById(R.id.calendar_next_button)

        AHCustomCalendar.selectedDate = Calendar.getInstance().time

        daysNameRecycler = view.findViewById(R.id.daysNameRecycler)

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

        if (days.size == 0){
            days = mutableListOf()
            days.add("Sun")
            days.add("Mon")
            days.add("Tue")
            days.add("Wed")
            days.add("Thu")
            days.add("Fri")
            days.add("Sat")
        }


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
                              dateSelectedListener: DateSelectedListener?
    ){
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
        setDateAdapter()

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
                        if (AHCustomCalendar.selectedDate == null && AHCustomCalendar.endDate == null){
                            AHCustomCalendar.selectedDate = dates[position].date!!
                        }else if (AHCustomCalendar.selectedDate != null && AHCustomCalendar.endDate == null){
                            AHCustomCalendar.endDate = dates[position].date!!

                            if(AHCustomCalendar.selectedDate!!.after(AHCustomCalendar.endDate)){
                                AHCustomCalendar.endDate = AHCustomCalendar.selectedDate.also { AHCustomCalendar.selectedDate =
                                    AHCustomCalendar.endDate
                                }
                            }
                        }else if(AHCustomCalendar.selectedDate != null && AHCustomCalendar.endDate != null){
                            AHCustomCalendar.selectedDate = dates[position].date!!
                            AHCustomCalendar.endDate = null
                        }

                    }
                    false -> {
                        AHCustomCalendar.selectedDate = dates[position].date!!
                        AHCustomCalendar.endDate = null
                    }
                }

                Log.d("asfvafv", "${AHCustomCalendar.selectedDate} ${AHCustomCalendar.endDate}")

                clickCalendar.time = AHCustomCalendar.selectedDate!!
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]

                calendarAdapter.updateList(shapeType)

                dateSelectedListener?.onDateSelected(
                    AHCustomCalendar.selectedDate!!,
                    AHCustomCalendar.endDate, isMultiSelect)


            }
        })
    }

    private fun setDateAdapter(){
        calendarAdapter = CalendarAdapter(context, dates, currentDate,
            isMultiSelect, shapeType, fontFamilyDates, selectedBubbleColor)
        recyclerView.adapter = calendarAdapter
    }


    fun setMonthFontFamily(fontFamilyMonth: Int){
        this.fontFamilyMonth = fontFamilyMonth
        txtCurrentMonth.typeface = ResourcesCompat.getFont(context, fontFamilyMonth)

    }

    fun setDaysFontFamily(fontFamilyDays: Int){
        this.fontFamilyDays = fontFamilyDays
        daysNameRecycler.adapter = getDaysAdapter(daysNameRecycler)
    }

    fun setDatesFontFamily(fontFamilyDates: Int){
        this.fontFamilyDates = fontFamilyDates

    }

    fun setShapeType(shapeType: String){
        this.shapeType = shapeType
        setDateAdapter()
    }

    fun setIsMultiSelect(isMultiSelect: Boolean){
        this.isMultiSelect = isMultiSelect
        setDateAdapter()
    }

    fun setSelectedBubbleColor(bubbleColor: String){
        this.selectedBubbleColor = bubbleColor
        setDateAdapter()
    }

    fun setEventList(eventList: ArrayList<EventModel>){
        this.eventList = eventList
        setUpCalendar(null, context, txtCurrentMonth, dateSelectedListener)

    }

    fun setDateCallBackListener(dateSelectedListener: DateSelectedListener?){
        this.dateSelectedListener = dateSelectedListener
        setUpCalendar(null, context, txtCurrentMonth, dateSelectedListener)

    }

}