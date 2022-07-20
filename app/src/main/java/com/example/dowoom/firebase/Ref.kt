package com.example.dowoom.firebase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Ref {
    //firebase 인스턴스 및 참조
    val database: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()

    val USER = "User"
    val CONNECT = "Connect"

    fun userRef() : DatabaseReference {
        return database.reference.child(USER)
    }

    fun connectRef() : DatabaseReference {
        return database.reference.child(CONNECT)
    }
    val CHATROOM = "ChatRoom"
    val MESSAGE = "Message"

    fun chatroomRef() : DatabaseReference {
        return database.reference.child(CHATROOM)
    }
    fun messageRef() : DatabaseReference {
        return database.reference.child(MESSAGE)
    }

    val COMU = "Comu"
    val COMMENT = "Comment"
    val GUEST = "Guest"
    val CONTENT = "Content"

    fun comunityRef() : DatabaseReference {
        return database.reference.child(COMU)
    }
    fun commentRef() : DatabaseReference {
        return database.reference.child(COMMENT)
    }
    fun guestRef() : DatabaseReference {
        return database.reference.child(GUEST)
    }
    fun contentRef() : DatabaseReference {
        return database.reference.child(CONTENT)
    }

    val GAME = "Game"
    val GAMECOUNT = "GameCount"
    val LADDER = "Ladder"
    val CIRCLE = "Circle"
    val FASTER = "Faster"
    val RESULT = "Result"

    fun gameRef() : DatabaseReference {
        return database.reference.child(GAME)
    }
    fun gameCountRef() : DatabaseReference {
        return database.reference.child(GAME)
    }
    fun gameLadderRef() : DatabaseReference {
        return database.reference.child(GAME).child(LADDER)
    }
    fun gameCircleRef() : DatabaseReference {
        return database.reference.child(GAME).child(CIRCLE)
    }
    fun gameFasterRef() : DatabaseReference {
        return database.reference.child(GAME).child(FASTER)
    }
    fun gameResultRef() : DatabaseReference {
        return database.reference.child(GAME).child(RESULT)
    }


    val auth: FirebaseUser
        get() = Firebase.auth.currentUser!!

    val storage: FirebaseStorage
        get() = FirebaseStorage.getInstance()

    val storageRef: StorageReference
        get() = storage.reference
}