package com.example.jawwna.alarmfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jawwna.R
import com.example.jawwna.datasource.model.AlarmEntity

class AlarmAdapter(
    private var alarms: List<AlarmEntity>,
    private val deleteItemClickListener: OnDeleteItemClickListener
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    interface OnDeleteItemClickListener {
        fun onItemClick(alarm: AlarmEntity) // Ensure the parameter matches the type you are using
    }

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val iconDelete: ImageView = itemView.findViewById(R.id.iconDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.textViewDate.text = alarm.date
        holder.textViewTime.text = alarm.time

        // Correctly reference deleteItemClickListener
        holder.iconDelete.setOnClickListener {
            deleteItemClickListener.onItemClick(alarm)
        }
    }

    override fun getItemCount() = alarms.size

    fun updateAlarms(newAlarms: List<AlarmEntity>) {
        alarms = newAlarms
        notifyDataSetChanged()
    }
}
