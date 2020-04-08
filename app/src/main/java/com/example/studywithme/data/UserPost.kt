package com.example.studywithme.data

data class UserPost(val userid: String, val postid: Int, val goalid: Int, val goalname: String, val content: String, val date: String, val like: Int, val taglist: ArrayList<String>) {
}
