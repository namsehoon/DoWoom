package com.example.dowoom.Util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.dowoom.firebase.Ref
import com.example.dowoom.model.talkModel.Message
import com.example.dowoom.repo.ChatRepo
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Flow


class HandleImage(val uriList: List<Uri>,val from:String,val to:String)  {

    val repo = ChatRepo()

    fun handleUpload() {
        //사진 업로드 and 위치 기억  /users/<userId>/profileImages/<image-file>

            val time = System.currentTimeMillis()/1000

            val storage = FirebaseStorage.getInstance().reference
            //파일 경로

            for (uri in uriList) {

                val fileRef = "Chat/${from}/${to}/${time}.jpeg"
                val imageRef = storage
                    .child(fileRef)

                val uploadTask = imageRef.putFile(uri)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        insertImageToDB(downloadUri)
                    }
                }


                Tasks.await(urlTask)
            }

    }

    fun insertImageToDB(uri:Uri)  {
        CoroutineScope(Dispatchers.IO).launch {

            repo.sendMessage(from,to,"이미지",uri)

        }
    }



}
