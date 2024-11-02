package com.example.chattingapp.feature.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chattingapp.R
import com.example.chattingapp.Screen
import com.example.chattingapp.ui.theme.Text2

@Composable
fun LogInScreen(navController: NavController){

    val viewModel : LoginViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()
    var email by remember{mutableStateOf("")}
    var password by remember{mutableStateOf("")}
    val context = LocalContext.current
    
    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value){
            is LoginState.Success -> {
                navController.navigate(Screen.Home.route)
            }
            is LoginState.Error ->{
                Toast.makeText(context,"Log in failed",Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
        
    }

    Scaffold (modifier = Modifier.fillMaxSize()){
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(value = email,
                onValueChange = {email = it},
                label = {Text2("Email")},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(value = password,
                onValueChange = {password = it},
                label = {Text2("Password")},
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.size(16.dp))

            if(uiState.value == LoginState.Loading){
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.Login(email, password) },
                    Modifier.fillMaxWidth(),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && (uiState.value == LoginState.Nothing || uiState.value == LoginState.Error)
                ) {
                    Text2(text = "Log In")
                }
                Spacer(modifier = Modifier.size(16.dp))
                TextButton(onClick = { navController.navigate(Screen.SignUp.route) }) {
                    Text2("Don't have an account? Sign Up")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogIn(){
    LogInScreen(navController = rememberNavController())
}