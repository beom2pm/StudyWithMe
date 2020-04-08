package com.example.studywithme.Board

import java.util.*

data class BoardData(val postID:String, val userID:String, val goalID:Int, val postContent:String, val date:String,val taglist:ArrayList<String>,val goalName:String)


//string 되는지 확인하고 Date로도 해보기