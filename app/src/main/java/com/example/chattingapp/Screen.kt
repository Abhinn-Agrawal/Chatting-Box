package com.example.chattingapp

sealed class Screen(val route:String) {
    object Home:Screen("home")
    object LogIn:Screen("login")
    object SignUp:Screen("signup")
}