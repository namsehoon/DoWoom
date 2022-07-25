package com.example.dowoom.Util

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.dowoom.firebase.Ref
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HandleGameMultiImages(val uriList:MutableList<Uri>) {



    val gameUid = Ref().gameLadderRef().push().key

    fun handleUpload() : String {

        Log.d("abcd","2. gameUid is : ${gameUid}")

        for (index in 0..uriList.size) { //사진 사이즈 만큼

            if (index >= uriList.size) {
                if (index < 6) {
                    Log.d("abcd","여기 들어온 시점 index : ${index}")
                    for (blank  in index..5) {
                        Log.d("abcd","사진이 없을 경우 index : ${blank}")
                        uploadImages(null,"꽝",blank, gameUid!!) //사진이 없을 경우
                    }
                }
            } else {
                var imageUri = uriList[index]
                uploadImages(imageUri,null,index, gameUid!!) //사진이 있을 경우
            }
        }

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

                val imageRef = Ref().storageRef.child("${fileRef}/${presentRef}")

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
                Ref().database.reference.child(filePresentResult).child(index.toString()).setValue("꽝").addOnSuccessListener {
                    Log.d("abcd","uploadToDB in HandleGameMultiImages is 꽝")
                }
            }
        }
    }

    private fun uploadToDB(filePresentResult: String, presentRef: String, index:Int) {
        Ref().database.reference.child(filePresentResult).child(index.toString()).setValue(presentRef).addOnSuccessListener {
            Log.d("abcd","4. uploadToDB in HandleGameMultiImages is 성공")
        }
    }


}
