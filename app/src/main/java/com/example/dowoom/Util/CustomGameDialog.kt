package com.example.dowoom.Util


import android.app.Activity
import android.app.Dialog
import android.app.GameManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.activity.game.PlayGameActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CustomGameDialog(val context: Context, val image:String,val gameUid:String ) {
    private val dialog = Dialog(context)
    private lateinit var ivPresent : ImageView
    private lateinit var btnOkAndFinish :Button
    private lateinit var btnOk : Button
    private lateinit var onDialogCustomClicked : onDialogCustomListener


    //파이어 베이스
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    fun onOkClickListener(listener: onDialogCustomListener) {
        onDialogCustomClicked = listener
    }



    fun setImage() {
        if (image != null && !image.equals("꽝")) {
            btnOkAndFinish.visibility = View.VISIBLE
            storageRef.child("Game/Ladder/${gameUid}/${image}").downloadUrl.addOnCompleteListener { task ->
                if (task.isComplete) {
                    Glide.with(context)
                        .load(task.result) // 이미지를 로드
                        .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
                        .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
                        .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
                        .into(ivPresent) //이미지를 보여줄 view를 지정

                }
            }
        } else{
            btnOk.visibility = View.VISIBLE
            ivPresent.setImageResource(R.drawable.ic_baseline_image_not_supported_24)
        }
    }

    fun start() {
        //타이틀바 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //xml 파일
        dialog.setContentView(R.layout.game_result_dialog)
        // dialog창 외의 화면을 눌렀을떄도 닫히지 않음
        dialog.setCancelable(false)

        //꽝 닫기
        btnOk = dialog.findViewById(R.id.btnOk)
        //사진 저장하고 닫기
        btnOkAndFinish = dialog.findViewById(R.id.btnOkAndFinish)
        ivPresent = dialog.findViewById(R.id.ivPresent)

        setImage()

        btnOk.setOnClickListener {
            dialog.dismiss()
            onDialogCustomClicked.onClicked()
        }

        btnOkAndFinish.setOnClickListener {
            //todo : 사진 저장
            //todo : 사진 저장
            //todo : 사진 저장
            dialog.dismiss()
            onDialogCustomClicked.onClicked()

        }

        dialog.show()
    }
    interface onDialogCustomListener{
        fun onClicked()
    }
}