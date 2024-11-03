package com.example.chattingapp.feature.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chattingapp.ui.theme.DarkGray
import com.example.chattingapp.ui.theme.HomeBackground
import com.example.chattingapp.ui.theme.LightBlue
import com.example.chattingapp.ui.theme.Text2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){

    val viewModel = hiltViewModel<HomeViewModel>()
    val channels = viewModel.channels.collectAsState()
    val addChannel = remember{ mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            Box(modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Blue)
                .clickable {
                    addChannel.value = true
                }
            ){
                Text2(text = "Add Channel",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        },
        containerColor = Color.Black
    ) {
        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()
        ){
            LazyColumn {
                item{
                    Text2("Messages", color = Color.Gray,fontSize = 40.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(16.dp))
                }
                item{
                    TextField(value = "", onValueChange = {},
                        placeholder = { Text2(text = "Search...")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(40.dp)),
                        textStyle = TextStyle(Color.LightGray),
                        colors = TextFieldDefaults.colors().copy(focusedContainerColor = DarkGray, unfocusedContainerColor = DarkGray),
                        leadingIcon = {Icon(imageVector = Icons.Filled.Search, contentDescription = null)}
                    )
                }
                items(channels.value){channel->
                    Column {
                        ChannelItem(channelName = channel.name, onClick = {navController.navigate("chat/${channel.id}&${channel.name}")},Modifier.padding(horizontal = 16.dp , vertical = 2.dp))
                    }
                }
            }
        }
    }
    if(addChannel.value){
        ModalBottomSheet(onDismissRequest = { addChannel.value = false },
            sheetState = sheetState ) {
            AddChannelDialog {
                viewModel.addChannel(it)
                addChannel.value = false
            }
        }
    }
}

@Composable
fun ChannelItem(channelName:String , onClick:() -> Unit, modifier: Modifier){
    Row(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DarkGray)
        .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(LightBlue.copy(alpha = 0.3f))
        ) {
            Text2(
                text = channelName[0].uppercase(),
                fontSize = 35.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text2(text = channelName, modifier = Modifier.padding(8.dp), color = Color.White)
    }
}

@Composable
fun AddChannelDialog(onAddChannel:(String) -> Unit){
    val channelName = remember{ mutableStateOf("") }
    Column (modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text2("Add Channel", fontWeight = FontWeight.Bold)
        Spacer(Modifier.padding(8.dp))
        TextField(value = channelName.value, onValueChange = {
            channelName.value = it},
            label = { Text2(text = "Channel Name")},
            singleLine = true,
        )
        Spacer(Modifier.padding(8.dp))
        Button(onClick = { onAddChannel(channelName.value) }) {
            Text2("Add")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen(){
    HomeScreen(navController = rememberNavController())
}