package com.example.dowoom.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

//todo : if sOrB가 true이면 == 서포터 else 수혜자
data class User(
    var age: Int? = 0,
    var nickname: String? = null,
    var stateMsg: String? = null,
    var popularity: Int? = 0,
    var status: Boolean = false,
    var email: String? = null,
    var sOrB: Boolean? = true,
    var Connect: Connect? = null




) {
    override fun toString(): String {
        return super.toString()
    }
}



//참조 : https://firebase.google.com/docs/database/android/read-and-write?hl=ko#kotlin+ktx_7
@IgnoreExtraProperties
data class updateUser( //(update?)
    var uid:String? = "",
    var nickname:String? = "",
    var stateMsg:String? = "",
    var sOrB:Boolean? = false
)  {
    //매핑
    @Exclude
    fun toMap():Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "nickname" to nickname,
            "stateMsg" to stateMsg,
            "sOrB" to sOrB
        )
    }
}


//
//data class User (var number:String? = null, var age:String? = null, var nickname:String? = null, var email:String? = null, var introduce:String? = null,
//                 var online:Boolean=false, var profileImg:String? = null, var sOrB:Int = 0, var gameCount:Int = 0,
//                 var stateMessage:String? = null, var popularity:Int = 0,
//                 var support: Array<Support>? = null, var reputation:Array<Reputation>? = null,
//                 var coupon:Array<Coupon>? = null, var block:Array<Block>? = null, var beneficiary:Array<Beneficiary>? = null,
//                 var receiveImg:Array<String>? = null, var alarm:Array<String>? = null)