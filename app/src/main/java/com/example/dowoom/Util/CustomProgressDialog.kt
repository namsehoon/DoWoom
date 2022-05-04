package com.example.dowoom.Util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.fragment.app.FragmentActivity
import com.example.dowoom.R

class CustomProgressDialog(activity: FragmentActivity) {

    private val dialog = Dialog(activity)

    fun start() {
        //노 타이틀
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

}