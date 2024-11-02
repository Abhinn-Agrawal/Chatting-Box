package com.example.chattingapp.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chattingapp.feature.home.HomeScreen
import com.example.chattingapp.model.Message
import com.example.chattingapp.ui.theme.ChatBackground
import com.example.chattingapp.ui.theme.Text2
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ChatScreen(navController: NavController, channelId :String) {

    Scaffold {
        Column(modifier = Modifier.fillMaxSize().padding(it).background(color = ChatBackground)){

            val viewModel: ChatViewModel = hiltViewModel()
            LaunchedEffect(key1 = true) {
                viewModel.listenForMessages(channelId)
            }
            val messages = viewModel.messages.collectAsState()

            ChatMessages(messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelId, message)
                }
            )
        }
    }
}

@Composable
fun ChatMessages(
    messages:List<Message>,
    onSendMessage:(String) -> Unit
){
    val hideKeyboard = LocalSoftwareKeyboardController.current
    val msg = remember{ mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn {
            items(messages){message ->
                ChatBubble(message = message)
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(8.dp)
            .background(color = Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(value = msg.value, onValueChange = {msg.value = it},
                modifier = Modifier.weight(1f),
                placeholder = { Text2(text = "Type message")},
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                       hideKeyboard?.hide()
                    }
                )
            )
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
                }
            ){
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message){
    val isCurrentUser = message.senderid == Firebase.auth.currentUser?.uid
    val bubbleColor = if(isCurrentUser){
        Color.Blue
    } else{
        Color.Green
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 8.dp)
    ){
        val align = if(isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
        Box(contentAlignment = align,
            modifier = Modifier
                .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.75).dp)
                .padding(8.dp)
                .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                .align(align)
        ){
            Text2(
                text = message.message,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen(){
    ChatScreen(navController = rememberNavController(),"test")
}