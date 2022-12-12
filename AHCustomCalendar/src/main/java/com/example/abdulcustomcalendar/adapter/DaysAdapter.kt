package com.example.abdulcustomcalendar.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abdulcustomcalendar.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DaysAdapter(private val context: Context,
                  private val data: MutableList<String>,
                  private val fontFamily: Int): RecyclerView.Adapter<DaysAdapter.ViewHolder2>() {


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder2 {
        val inflater = LayoutInflater.from(context)
        return ViewHolder2(inflater.inflate(R.layout.custom_list_view2, parent, false))
    }

    override fun getItemCount(): Int = 7

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.txtDayInWeek.text = data[position]
    }

    inner class ViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtDayInWeek: TextView = itemView.findViewById(R.id.txt_day)
        init {
            txtDayInWeek.typeface = ResourcesCompat.getFont(context, fontFamily)
        }
    }

}