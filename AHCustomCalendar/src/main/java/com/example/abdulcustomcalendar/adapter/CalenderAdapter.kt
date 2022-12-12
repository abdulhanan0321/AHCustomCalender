package com.example.abdulcustomcalendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abdulcustomcalendar.AHCustomCalendar
import com.example.abdulcustomcalendar.R
import com.example.abdulcustomcalendar.model.CalenderModel
import com.example.abdulcustomcalendar.util.ImportantFunction
import java.util.*


class CalendarAdapter(private val context: Context,
                      private var data: ArrayList<CalenderModel>,
                      private val currentDate: Calendar,
                      private val isMultiSelect: Boolean,
                      private var shapeType: String,
                      private val fontFamily: Int,
                      private val selectedBubbleColor: String): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]

    interface OnItemClickListener {
        fun onItemClick(position: Int, isMultiSelect: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_list_view, parent, false)
        val holder = ViewHolder(view)

        holder.linearLayout.setOnClickListener {
            val positions = holder.adapterPosition
            mListener?.onItemClick(positions, isMultiSelect)
        }

        return  holder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val model = data[position]

        val cal = Calendar.getInstance()
        if (!model.isForceEmpty) {
            if(model.eventList.size > 0){
                holder.bubble.visibility = View.VISIBLE
                holder.bubble.setImageResource(model.eventList[0].eventImage)
            }else {
                holder.bubble.visibility = View.GONE
            }

            cal.time = model.date!!

            val displayMonth = cal[Calendar.MONTH]
            val displayYear = cal[Calendar.YEAR]
            val displayDay = cal[Calendar.DAY_OF_MONTH]

            holder.txtDay.text = displayDay.toString()

            /** checks for selected, default & disabled dates*/
            if (displayYear >= currentYear) {
                if (displayMonth >= currentMonth || displayYear > currentYear) {
                    if (displayDay >= currentDay || displayMonth > currentMonth || displayYear > currentYear) {

                        if (!isMultiSelect) { /** check if multi selection is disabled*/
                            if (ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.selectedDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)) /** check if selected date and list date are same*/
                                makeItemSelected(holder, "normal")
                            else
                                makeItemDefault(holder)
                        }else { /** if multi selection is enable*/

                            if (AHCustomCalendar.selectedDate != null
                                && AHCustomCalendar.endDate == null
                                && ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.selectedDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)) { /** check if start date and list date are same*/
                                makeItemSelected(holder, "normal")

                            }else if(AHCustomCalendar.selectedDate == null
                                && AHCustomCalendar.endDate != null
                                && ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.endDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)){ /** check if end date and list date are same*/
                                makeItemSelected(holder, "normal")

                            } else if (AHCustomCalendar.selectedDate != null
                                && AHCustomCalendar.endDate != null){

                                if (shapeType == "rectangle"){
                                    if (ImportantFunction.dateDiff(AHCustomCalendar.selectedDate!!, AHCustomCalendar.endDate!!, model.date!!)) { /** check for in between dates*/
                                        makeItemWithInRange(holder, shapeType)
                                    }else if (ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.selectedDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)) {
                                        makeItemSelected(holder, "startDate")
                                    }else if (ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.endDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)) {
                                        makeItemSelected(holder, "endDate")
                                    }
                                }else {
                                    if (ImportantFunction.dateDiff(AHCustomCalendar.selectedDate!!, AHCustomCalendar.endDate!!, model.date!!)) { /** check for in between dates*/
                                        makeItemWithInRange(holder, shapeType)
                                    }else if (ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.selectedDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)) {
                                        makeItemSelected(holder, "normal")
                                    }else if (ImportantFunction.getDateOnlyFromFullDate(AHCustomCalendar.endDate!!) == ImportantFunction.getDateOnlyFromFullDate(model.date!!)) {
                                        makeItemSelected(holder, "normal")
                                    }
                                }


                            }else {
                                makeItemDefault(holder)
                            }
                        }

                    } else {
                        makeItemDisabled(holder)
                    }
                } else {
                    makeItemDisabled(holder)
                }
            } else {
                makeItemDisabled(holder)
            }


        }else {
            holder.txtDay.text = ""
            holder.bubble.visibility = View.GONE
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(shapeType: String){
        this.shapeType = shapeType
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtDay: TextView = itemView.findViewById(R.id.txt_date)
        var bubble: ImageView = itemView.findViewById(R.id.bubble)
        var linearLayout: ConstraintLayout = itemView.findViewById(R.id.calendar_linear_layout)

        init {
            txtDay.typeface = ResourcesCompat.getFont(context, fontFamily)
        }
    }

    private fun makeItemDisabled(holder: ViewHolder) {
        holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.grey))
        holder.linearLayout.setBackgroundColor(Color.WHITE)
        holder.linearLayout.isEnabled = false
    }

    private fun makeItemSelected(holder: ViewHolder, type:String) {
        holder.txtDay.setTextColor(Color.parseColor("#FFFFFF"))
        when(type){
            "normal" -> {
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.drawable.circle)
                holder.linearLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedBubbleColor))
            }
            "startDate" -> {
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.drawable.start_date_circle)
                holder.linearLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedBubbleColor))
            }
            "endDate" -> {
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.drawable.end_date_circle)
                holder.linearLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedBubbleColor))
            }
        }
        holder.linearLayout.isEnabled = false
    }

//    private fun makeCurrentDate(holder: ViewHolder) {
//        holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.purple_200))
//        holder.linearLayout.setBackgroundColor(Color.WHITE)
//        holder.linearLayout.isEnabled = true
//    }

    private fun makeItemWithInRange(holder: ViewHolder, shapeType: String) {
        holder.txtDay.setTextColor(Color.BLACK)
        when(shapeType){
            "rectangle" -> {
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.drawable.rectangle_dim_blue)
                holder.linearLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedBubbleColor))
            }
            "round" -> {
                holder.linearLayout.background = ContextCompat.getDrawable(context, R.drawable.circle_dim_blue)
                holder.linearLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(selectedBubbleColor))
            }
        }
        holder.linearLayout.isEnabled = true
    }

    private fun makeItemDefault(holder: ViewHolder) {
        holder.txtDay.setTextColor(Color.BLACK)
        holder.linearLayout.setBackgroundColor(Color.WHITE)
        holder.linearLayout.isEnabled = true
    }
}