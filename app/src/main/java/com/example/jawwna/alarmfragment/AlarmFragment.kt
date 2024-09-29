package com.example.jawwna.alarmfragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jawwna.R
import com.example.jawwna.alarmfragment.adapter.AlarmAdapter
import com.example.jawwna.alarmfragment.viewmodel.AlarmViewModel
import com.example.jawwna.alarmfragment.viewmodel.AlarmViewModelFactory
import com.example.jawwna.databinding.FragmentAlarmBinding
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.repository.Repository
import com.example.jawwna.homescreen.adapter.DailyWeatherForecastAdapter
import kotlinx.coroutines.launch
import java.util.*

class AlarmFragment : Fragment() {

    private lateinit  var binding: FragmentAlarmBinding
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmrRecyclerView: RecyclerView
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var selectedYear=0
    private var selectedMonth=0
    private var selectedDay =0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.cardView.visibility= View.GONE

        alarmViewModel = ViewModelProvider(
            this,
            AlarmViewModelFactory(Repository.getRepository(requireActivity().application))
        ).get(AlarmViewModel::class.java)


        // Initialize the DailyWeatherForecastAdapter and RecyclerView
        alarmrRecyclerView = binding.recyclerViewAlarms
        // Set the LayoutManager for the RecyclerView
        alarmrRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Initialize the adapter with an empty list for now
        alarmAdapter = AlarmAdapter(
            emptyList(),
            object : AlarmAdapter.OnDeleteItemClickListener {
                override fun onItemClick(alarm: com.example.jawwna.datasource.model.AlarmEntity) {
                    alarmViewModel.deleteAlarm(alarm)
                    // Handle the item click, show a Toast or navigate to another screen
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.item_clicked, alarm.date),
                        Toast.LENGTH_SHORT
                    ).show() // Don't forget to call show() on the Toast
                }
            }
        )
        // Set the adapter to the RecyclerView
        alarmrRecyclerView.adapter = alarmAdapter


        alarmViewModel.loadAlarms()
        lifecycleScope.launch {
            alarmViewModel.alarms.collect { alarms ->
                alarmAdapter.updateAlarms(alarms)
            }
        }


        // Handle time selection
        binding.buttonSelectTime.setOnClickListener {
            showTimePicker(selectedYear, selectedMonth, selectedDay)
        }

        // Handle date selection
        binding.buttonSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Handle save action
        binding.buttonSave.setOnClickListener {
            saveAlarm()
            binding.cardView.visibility= View.GONE
        }

        // Handle cancel action
        binding.buttonCancel.setOnClickListener {
            binding.cardView.visibility= View.GONE
            binding.buttonSelectDate.text = getString(R.string.select_date)
            binding.buttonSelectTime.text = getString(R.string.select_time)
            Toast.makeText(requireContext(), "Action canceled!", Toast.LENGTH_SHORT).show()
        }

        // Handle Floating Action Button click to show/hide the CardView
        binding.fabShowCard.setOnClickListener {
            if (binding.cardView.visibility == View.GONE) {
                binding.cardView.visibility = View.VISIBLE // Show the card
            } else {
                binding.cardView.visibility = View.GONE // Hide the card
            }
        }

    }


    private fun showDatePicker() {
        // Get the date limits from the ViewModel
        val dateLimits = alarmViewModel.getDateLimits()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Initialize the DatePickerDialog
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            binding.buttonSelectDate.text = selectedDate // Update button text
            this.selectedYear=selectedYear
            this.selectedMonth=selectedMonth
            this.selectedDay=selectedDay
        }, year, month, day)

        // Set minimum and maximum dates if limits are provided
        dateLimits?.let { (minDate, maxDate) ->
            datePickerDialog.datePicker.minDate = minDate.time
            datePickerDialog.datePicker.maxDate = maxDate.time
        }

        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun showTimePicker(year: Int, month: Int, day: Int) {
        if (year == null || month == null || day == null || year == 0 || month == 0 || day == 0) {
            Toast.makeText(requireContext(), getString(R.string.invalid_date), Toast.LENGTH_SHORT).show()
            return
        }else {


            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            // Create a new calendar instance for the selected date
            val selectedDateCalendar = Calendar.getInstance().apply {
                set(year, month, day)
            }

            val timePickerDialog =
                TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                    // Create a new calendar instance for the selected time
                    val selectedTimeCalendar = Calendar.getInstance().apply {
                        set(year, month, day, selectedHour, selectedMinute)
                    }

                    // Compare selected time with the current time
                    if (selectedTimeCalendar.timeInMillis < System.currentTimeMillis()) {
                        // Show an error message if the selected time is in the past
                        Toast.makeText(
                            requireContext(),
                            "You cannot select a past time.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                        binding.buttonSelectTime.text = selectedTime // Update button text
                    }
                }, currentHour, currentMinute, true)

            // Check if the selected date is today
            if (selectedDateCalendar.timeInMillis <= System.currentTimeMillis()) {
                // If it's today, set the minimum time to the current time
                timePickerDialog.updateTime(currentHour, currentMinute)
            }

            timePickerDialog.show()
        }
    }


    private fun saveAlarm() {
        if (!selectedDate.isNullOrEmpty() && !selectedTime.isNullOrEmpty() ||selectedTime ==getString(R.string.select_time) || selectedDate ==getString(R.string.select_date)) {
            // Create an AlarmEntity with the selected date and time
            val alarmEntity = AlarmEntity( selectedDate!!, selectedTime!!) // Use proper ID management
            alarmViewModel.saveAlarm(alarmEntity) // Save alarm through ViewModel
            Toast.makeText(requireContext(), getString(R.string.alarm_saved), Toast.LENGTH_SHORT).show()
            binding.buttonSelectDate.text = getString(R.string.select_date)
            binding.buttonSelectTime.text = getString(R.string.select_time)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.please_select_both_date_and_time), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
/*

   private fun showTimePicker() {
       val calendar = Calendar.getInstance()
       val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
       val currentMinute = calendar.get(Calendar.MINUTE)

       val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
           // Create a new calendar instance for the selected time
           val selectedTimeCalendar = Calendar.getInstance()
           selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
           selectedTimeCalendar.set(Calendar.MINUTE, selectedMinute)

           // Compare selected time with the current time
           if (selectedTimeCalendar.timeInMillis < calendar.timeInMillis) {
               // Show an error message or handle the case when the time is in the past
               Toast.makeText(requireContext(), "You cannot select a past time.", Toast.LENGTH_SHORT).show()
           } else {
               selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
               binding.buttonSelectTime.text = selectedTime // Update button text
           }
       }, currentHour, currentMinute, true)

       timePickerDialog.show()
   }

*/