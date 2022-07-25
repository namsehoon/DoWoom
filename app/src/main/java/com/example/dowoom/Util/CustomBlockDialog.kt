package com.example.dowoom.Util

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.dowoom.R

class CustomBlockDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var tvContent : TextView
    private lateinit var okBtn : Button
    private lateinit var onDialogCustomClicked : onDialogCustomListener

    fun onOkClickListener(listener: onDialogCustomListener) {
        onDialogCustomClicked = listener
    }
    fun start(content:String) {
        //타이틀바 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //xml 파일
        dialog.setContentView(R.layout.block_dialog)
        // dialog창 외의 화면을 눌렀을떄도 닫히지 않음
        dialog.setCancelable(false)

        tvContent = dialog.findViewById(R.id.content)
        tvContent.text = content

        okBtn = dialog.findViewById(R.id.ok)

        okBtn.setOnClickListener {
            onDialogCustomClicked.onClicked()
            dialog.dismiss()
        }

        dialog.show()
    }

    interface onDialogCustomListener{
        fun onClicked()
    }
}