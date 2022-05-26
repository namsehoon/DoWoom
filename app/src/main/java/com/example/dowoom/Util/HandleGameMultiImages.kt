package com.example.dowoom.Util

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HandleGameMultiImages(val uriList:ArrayList<Uri>) {


    private val storage = FirebaseStorage.getInstance().reference
    private val ref =  FirebaseDatabase.getInstance().reference
    val gameUid = ref.child("Game").child("Ladder").push().key

    fun handleUpload() : String {

        Log.d("abcd","2. gameUid is : ${gameUid}")

        var index = 0
        while(index < uriList.size) { //사진 사이즈 만큼
            println("item at $index is ${uriList[index]}")

            var imageUri = uriList[index]
            uploadImages(imageUri,null,index, gameUid!!) //사진이 있을 경우
            index++

        }
        if (index < 6) {
            Log.d("abcd","여기 들어온 시점 index : ${index}")
            while (index < 6) {
                Log.d("abcd","사진이 없을 경우 index : ${index}")
                uploadImages(null,"꽝",index, gameUid!!) //사진이 없을 경우
                index++
            }

        }
        Log.d("abcd","빠져나온 index : ${index}")

        return gameUid!!


    }

    private fun uploadImages(uri : Uri? = null, nothing:String? = null, index:Int, gameUid:String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("abcd","3. uploadImages")
            //db 저장용
            val filePresentResult = "Game/Result/${gameUid}/"
            //파일 업로드 용
            val fileRef = "Game/Ladder/${gameUid}/"
            val presentRef = "${index}.jpeg"

            //사진이 있을 경우
            if (uri != null) {

                val imageRef = storage.child("${fileRef}/${presentRef}")

                val uploadTask = imageRef.putFile(uri)

                val uriTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.d("abcd","downloaduri is : ${downloadUri}")

                        //여기서 선물들 db에 올림
                        uploadToDB(filePresentResult,presentRef, index)
                    }
                }

                Tasks.await(uriTask)
            } else  { //사진이 없을 경우 (꽝)
                ref.child(filePresentResult).child(index.toString()).setValue("꽝").addOnSuccessListener {
                    Log.d("abcd","uploadToDB in HandleGameMultiImages is 꽝")
                }
            }
        }
    }

    private fun uploadToDB(filePresentResult: String, presentRef: String, index:Int) {
        ref.child(filePresentResult).child(index.toString()).setValue(presentRef).addOnSuccessListener {
            Log.d("abcd","4. uploadToDB in HandleGameMultiImages is 성공")
        }
    }


}
