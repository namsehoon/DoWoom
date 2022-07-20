package com.example.dowoom.Util

import android.util.Log
import java.util.*
import java.util.concurrent.ThreadLocalRandom.current

class TimeStampToDate(val date:Date) {

    fun getsome() {
        val timestamp = date
//        ,val currentDate:Date
//        val current = currentDate


        Log.d("Abcd","timestamp : ${timestamp}")
        Log.d("Abcd","timestamp time : ${timestamp.time}")
        Log.d("Abcd","timestamp  year : ${timestamp.year}")
        Log.d("Abcd","timestamp  month : ${timestamp.month}")
        Log.d("Abcd","timestamp  day : ${timestamp.day}")
        Log.d("Abcd","timestamp  hours : ${timestamp.hours}")
        Log.d("Abcd","timestamp  minutes : ${timestamp.minutes}")
        Log.d("Abcd","timestamp  seconds : ${timestamp.seconds}")
    }


}

//fun timeAgoSinceDate(date:Date, currentDate:Date, numericDates:Boolean) : String {
//    val calendar = Calendar.getInstance()
//    calendar.time = date
//    val now = currentDate
//
//    val leftTimestamp =
//
//    if (components.year! >= 2) {
//        return "${components.year!} years ago"
//    } else if (components.year! >= 1){
//        if (numericDates){ return "1 year ago"
//        } else { return "Last year" }
//    } else if (components.month! >= 2) {
//        return "\(components.month!) months ago"
//    } else if (components.month! >= 1){
//        if (numericDates){ return "1 month ago"
//        } else { return "Last month" }
//    } else if (components.weekOfYear! >= 2) {
//        return "\(components.weekOfYear!) weeks ago"
//    } else if (components.weekOfYear! >= 1){
//        if (numericDates){ return "1 week ago"
//        } else { return "Last week" }
//    } else if (components.day! >= 2) {
//        return "\(components.day!) days ago"
//    } else if (components.day! >= 1){
//        if (numericDates){ return "1 day ago"
//        } else { return "Yesterday" }
//    } else if (components.hour! >= 2) {
//        return "\(components.hour!) hours ago"
//    } else if (components.hour! >= 1){
//        if (numericDates){ return "1 hour ago"
//        } else { return "An hour ago" }
//    } else if (components.minute! >= 2) {
//        return "\(components.minute!) minutes ago"
//    } else if (components.minute! >= 1){
//        if (numericDates){ return "1 minute ago"
//        } else { return "A minute ago" }
//    } else if (components.second! >= 3) {
//        return "\(components.second!) seconds ago"
//    } else { return "Just now" }
//}
