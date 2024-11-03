package com.example.chattingapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chattingapp.feature.auth.login.LogInScreen
import com.example.chattingapp.feature.auth.signin.SignUpScreen
import com.example.chattingapp.feature.chat.ChatScreen
import com.example.chattingapp.feature.home.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp(){
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val start = if(currentUser!=null) Screen.Home.route else Screen.LogIn.route

        NavHost(navController = navController, startDestination = start){
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.LogIn.route){
                LogInScreen(navController)
            }
            composable(Screen.SignUp.route){
                SignUpScreen(navController)
            }
            composable("chat/{channelId}&{channelName}",
                arguments = listOf(
                    navArgument("channelId"){
                    type = NavType.StringType
                    },
                    navArgument("channelName"){
                        type = NavType.StringType
                    }
                )
            ){
                val channelId = it.arguments?.getString("channelId") ?: ""
                val channelName = it.arguments?.getString("channelName") ?: ""
                ChatScreen(navController,channelId, channelName)
            }
        }
    }
}