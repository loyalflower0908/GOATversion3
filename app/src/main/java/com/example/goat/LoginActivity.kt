package com.example.goat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : ComponentActivity() {

    // Firebase
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val RC_SIGN_IN = 1313

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen { googleLogin() }
        }
    }

    // 로그인 객체 생성
    fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignIn()
    }

    // 구글 회원가입
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "구글 회원가입에 실패하였습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }


        } else {
            /*no-op*/
        }
    }

    // account 객체에서 id 토큰 가져온 후 Firebase 인증
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                toMainActivity(auth.currentUser)
            }
        }
    }

    private fun toMainActivity(user: FirebaseUser?) {
        if (user != null) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    @Composable
    fun LoginScreen(content: () -> Unit) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.login)
        )
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = 1,
            isPlaying = true,
            //속도(부호 = 방향)
            speed = 1f,
            restartOnPlay = false
        )
        //최대 117.99 프레임
        val currentFrame = composition?.getFrameForProgress(progress)
        var dpChange by remember { mutableStateOf(0.dp) }
        val dpAnimateAsState: Dp by animateDpAsState(
            targetValue = dpChange,
            animationSpec = tween(1000, easing = LinearOutSlowInEasing), label = ""
        )
        var textVisible by remember { mutableStateOf(false) }
        var buttonVisible by remember { mutableStateOf(false) }
        val alpha: Float by animateFloatAsState(targetValue = if (textVisible) 1f else 0f, animationSpec = tween(1200, easing = LinearOutSlowInEasing))
        Surface(color = Color.Black) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 32.dp, end = 32.dp),
                verticalArrangement = Arrangement.Center

            ) {
                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(300.dp)
                )
                if (dpAnimateAsState == 60.dp) {
                    textVisible = true
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(40.dp)
                            .wrapContentWidth()
                    ) {
                        Text(
                            text = "Groupware Of All Teachers",
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            fontSize = 24.sp,
                            modifier = Modifier.graphicsLayer(alpha = alpha).align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                } else {
                    Spacer(modifier = Modifier.size(dpAnimateAsState))
                }
                AnimatedVisibility(visible = buttonVisible, enter = fadeIn(tween(1400))) {
                    SignInGoogleButton({
                        content()
                    }, dpAnimateAsState)
                }
                Spacer(modifier = Modifier.size(dpChange))
                if (currentFrame != null && currentFrame > 40) {
                    dpChange = 60.dp
                    buttonVisible = true
                }
            }
        }
    }

    @Composable
    fun SignInGoogleButton(onClick: () -> Unit, dpChange: Dp) {
        Surface(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .height(dpChange)
                .background(Color.Black),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            shape = MaterialTheme.shapes.small,
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
                Icon(
                    painter = painterResource(id = R.drawable.icon_google),
                    contentDescription = "Google sign button",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "구글 아이디로 시작하기",
                    color = Color.Gray,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}