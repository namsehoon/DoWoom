package com.example.dowoom.Util

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageSaveDialog(val context: Context) {
    private val dialog = Dialog(context)
    private lateinit var iv : ImageView
    private lateinit var save : TextView
    private lateinit var cancel : TextView
    private lateinit var onDialogCustomClicked : onDialogCustomListener

    fun onOkClickListener(listener: ImageSaveDialog.onDialogCustomListener) {
        onDialogCustomClicked = listener
    }
    fun start(content:String) {
        //타이틀바 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //xml 파일
        dialog.setContentView(R.layout.popup_image_dialog)
        // dialog창 외의 화면을 눌렀을떄도 닫히지 않음
        dialog.setCancelable(false)

        iv = dialog.findViewById(R.id.ivImage)

        CoroutineScope(Dispatchers.Main).launch {
            Glide.with(context)
                .load(content) // 이미지를 로드
                .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
                .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
                .into(iv) //이미지를 보여줄 view를 지정
        }
        save = dialog.findViewById(R.id.tvSave)
        cancel = dialog.findViewById(R.id.tvClose)

        save.setOnClickListener {
            onDialogCustomClicked.onClicked()
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    interface onDialogCustomListener{
        fun onClicked()
    }
}