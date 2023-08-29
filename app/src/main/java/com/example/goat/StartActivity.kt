package com.example.goat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.goat.ui.theme.GOATTheme
import com.google.firebase.auth.FirebaseAuth

class StartActivity : ComponentActivity() {
    // Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.tryLogin(this)
            if (auth.currentUser != null) {
                // 이미 로그인되어 있으면 MainActivity로 이동
                finish()
                startActivity(Intent(this@StartActivity, MainActivity::class.java))
            } else {
                // 로그인되어 있지 않으면 LoginActivity로 이동
                finish()
                startActivity(Intent(this@StartActivity, LoginActivity::class.java))
            }
        }
    }
}
