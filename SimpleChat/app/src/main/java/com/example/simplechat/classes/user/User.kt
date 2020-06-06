package com.example.simplechat.classes.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
class User(val uid: String,val username:String,val phone:String,val profileImageUrl:String) : Parcelable{
    constructor() : this("","","","https://firebasestorage.googleapis.com/v0/b/simplechat-15410.appspot.com/o/images%2Fb4624b2a-d810-49ed-8ad6-9d7d077d3cd6?alt=media&token=4bf35423-b835-41fe-b1e4-ca2ecfe10fa2")
}