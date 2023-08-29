package com.example.goat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goat.ui.theme.GOATTheme

@Composable
fun LoginScreen(content: () -> Unit) {
    Surface(color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp),
            verticalArrangement = Arrangement.Center

        ) {
            Greeting(text = "시작하시겠습니까?")
            SignInGoogleButton { content() }
        }
    }
}

@Composable
fun Greeting(text: String) {
    Text(text = text, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun SignInGoogleButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick).fillMaxWidth(),
        // border = BorderStroke(width = 1.dp, color = Color.LightGray),
//        color = MaterialTheme.colors.surface,
//        shape = MaterialTheme.shapes.small,
//        elevation = 10.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(
                start = 14.dp,
                end = 12.dp,
                top = 11.dp,
                bottom = 11.dp
            )
        ) {
            Icon(painter = painterResource(id = R.drawable.icon_google), contentDescription = "Google sign button", tint = Color.Unspecified, modifier = Modifier.size(35.dp))
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "Sign in with Google",  color = Color.Gray, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}