package com.example.chattingapp.model

data class Message(
    val id:String = "",
    val senderid:String = "",
    val message:String? = "",
    val createdAt:Long = System.currentTimeMillis(),
    val senderName:String? = null,
    val senderImage:String? = null,
    val imageurl :String? = null
)
