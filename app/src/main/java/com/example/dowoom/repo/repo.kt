package com.example.dowoom.repo

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

interface repo {
    //firebase 인스턴스 및 참조
    val database: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()

    val auth: FirebaseUser
    get() = Firebase.auth.currentUser!!

    //root
    val rootRef : DatabaseReference
    get() = database.reference

/////////////

    val storage: FirebaseStorage
        get() = FirebaseStorage.getInstance()

    val storageRef: StorageReference
    get() = storage.reference

}