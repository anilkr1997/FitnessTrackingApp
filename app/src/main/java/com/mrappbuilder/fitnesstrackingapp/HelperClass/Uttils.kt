package com.mrappbuilder.fitnesstrackingapp.HelperClass

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Uttils {
    fun currentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}