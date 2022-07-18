package com.example.dowoom.Util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class HandleProfileImage(val context: Context, val bitmap: Bitmap) {

    init {
        handleUpload(bitmap)
    }


    fun handleUpload(bitmap: Bitmap) {
        //압축하기
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)

        //로그인한 유저 uid
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        //사진 업로드 and 위치 기억  /users/<userId>/profileImages/uid.jpeg
        val storage = FirebaseStorage.getInstance().reference
            .child("User")
            .child(uid!!)
            .child("profileImages")
            .child(uid.plus(".jpeg"))

        //storage 저장
        storage.putBytes(stream.toByteArray())
            .addOnSuccessListener {
                //내 프로필 참조 위치 저장
                getDownloadUrl(storage)
            }
            .addOnFailureListener { error ->
                Log.d("abcd","프로필사진 업데이트 실패 : ${error.message}")
            }
    }

    fun getDownloadUrl(reference: StorageReference) {
        //downloadUrl : 객체를 다운로드하는 데 사용할 수 있는 URL
        reference.downloadUrl
            .addOnSuccessListener(OnSuccessListener {  uri ->
                Log.d("abcd","프로필사진 uri is : ${uri}")
                setUserProfileurl(uri)
            })
    }

    fun setUserProfileurl(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("abcd","setUserProfileurl in handleProfileimage() is : ${uri}")

        val profileUpdate = userProfileChangeRequest {
            photoUri = uri
        }

        user!!.updateProfile(profileUpdate)
            .addOnSuccessListener(OnSuccessListener {
                Log.d("abcd","프로필 업데이트 성공")
            })
            .addOnFailureListener { error ->
                Log.d("abcd","프로필 업데이트 실패 : ${error.message}")
            }
    }
}