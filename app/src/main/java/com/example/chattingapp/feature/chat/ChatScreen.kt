package com.example.chattingapp.feature.chat

import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.chattingapp.R
import com.example.chattingapp.Screen
import com.example.chattingapp.model.Message
import com.example.chattingapp.ui.theme.DarkGray
import com.example.chattingapp.ui.theme.Purple
import com.example.chattingapp.ui.theme.Text2
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(navController: NavController, channelId :String) {

    Scaffold(containerColor = Color.Black) {

        val viewModel: ChatViewModel = hiltViewModel()
        val chooserDialog = remember{ mutableStateOf(false) }
        val cameraImageUri = remember {
            mutableStateOf<Uri?>(null)
        }
        val cameraImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) {success ->
            if(success){
                cameraImageUri.value?.let{
                    viewModel.sendImageMessage(it , channelId)
                }
            }
        }

        fun createImageUri():Uri{
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = ContextCompat.getExternalFilesDirs(
                navController.context, Environment.DIRECTORY_PICTURES
            ).first()
            return FileProvider.getUriForFile(
                navController.context,
                "${navController.context.packageName}.provider",
                File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir).apply {
                    cameraImageUri.value = Uri.fromFile(this)
                }
            )
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()) {isGranted ->
            if(isGranted)
                cameraImageLauncher.launch(createImageUri())
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ){

            LaunchedEffect(key1 = true) {
                viewModel.listenForMessages(channelId)
            }
            val messages = viewModel.messages.collectAsState()

            ChatMessages(messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelId, message)
                },
                onImageClicked = {chooserDialog.value = true}
            )
        }

        if(chooserDialog.value)
            ContentSelectionDialog(
                onCameraSelected = {
                    chooserDialog.value = false
                    if(navController.context.checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED)
                        cameraImageLauncher.launch(createImageUri())
                    else
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                }
                , onGallerySelected = {
                    chooserDialog.value = false
                }
            )
    }
}

@Composable
fun ContentSelectionDialog(onCameraSelected:() -> Unit, onGallerySelected:() -> Unit){
    AlertDialog(onDismissRequest = { /*TODO*/ },
        confirmButton = { TextButton(onClick = onCameraSelected){Text2("Camera",color = Color.White)} },
        dismissButton = {TextButton(onClick = onGallerySelected){Text2("Gallery",color = Color.White)}},
        title = {Text2("Select your source")},
        text = {Text2("Would you like to pick an image from gallery or use the camera?")}
    )

}

@Composable
fun ChatMessages(
    messages:List<Message>,
    onSendMessage:(String) -> Unit,
    onImageClicked:() -> Unit
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
            .background(color = DarkGray)
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                msg.value = ""
                onImageClicked()
            }
            ){
                Image(painter = painterResource(id = R.drawable.attach), contentDescription = "attach")
            }

            TextField(value = msg.value, onValueChange = {msg.value = it},
                modifier = Modifier.weight(1f),
                placeholder = { Text2(text = "Type message")},
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                       hideKeyboard?.hide()
                    }
                ),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = DarkGray,
                    unfocusedContainerColor = DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White
                )
            )
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
                }
            ){
                Image(painter = painterResource(id = R.drawable.send), contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message){
    val isCurrentUser = message.senderid == Firebase.auth.currentUser?.uid
    val bubbleColor = if(isCurrentUser){
        Purple
    } else{
        DarkGray
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 8.dp)
    ){
        val align = if(isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
        Row(modifier = Modifier
            .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.75).dp)
            .padding(8.dp)
            .align(align),
            verticalAlignment = Alignment.CenterVertically
        ){if(!isCurrentUser) {
            Image(
                painter = painterResource(id = R.drawable.friend),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
            Box(modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
            ) {
                if (message.imageurl != null) {
                    AsyncImage(
                        model = message.imageurl,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    Text2(text = message.message?.trim() ?: "", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen(){
    ChatScreen(navController = rememberNavController(),"test")
}