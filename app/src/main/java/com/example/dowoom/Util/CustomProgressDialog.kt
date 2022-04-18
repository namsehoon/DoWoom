package com.example.dowoom.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.dowoom.R

class CustomProgressDialog(context: Context) {

    private val dialog = Dialog(context)

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