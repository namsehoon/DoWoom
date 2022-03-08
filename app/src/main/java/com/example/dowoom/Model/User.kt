package com.example.dowoom.Model

data class User (var age:Int = 0, var nickname:String? = null, var stateMsg:String? = null, var popularity:String? = null, var status:Boolean = false, var email:String? = null, var sOrB:Boolean)



//
//data class User (var number:String? = null, var age:String? = null, var nickname:String? = null, var email:String? = null, var introduce:String? = null,
//                 var online:Boolean=false, var profileImg:String? = null, var sOrB:Int = 0, var gameCount:Int = 0,
//                 var stateMessage:String? = null, var popularity:Int = 0,
//                 var support: Array<Support>? = null, var reputation:Array<Reputation>? = null,
//                 var coupon:Array<Coupon>? = null, var block:Array<Block>? = null, var beneficiary:Array<Beneficiary>? = null,
//                 var receiveImg:Array<String>? = null, var alarm:Array<String>? = null)