package com.example.goat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goat.ui.theme.GOATTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //다크모드 컬러 기억
            val activity = LocalContext.current as? Activity
            val context = LocalContext.current
            val sharedPref = remember { activity?.getPreferences(Context.MODE_PRIVATE) }
            //컨텐츠 컬러와 백그라운드 컬러
            var contentColor by remember { mutableStateOf(Color.Black) }
            var backgroundColor by remember { mutableStateOf(Color.White) }
            //다크 모드 트리거
            var isDark by remember {
                val getDark = sharedPref?.getBoolean("Dark", false) ?: false
                mutableStateOf(getDark)
            }
            //다크 모드에 따른 색상 변경
            if (isDark) {
                contentColor = Color.White
                backgroundColor = Color(0xff121212)
            } else {
                contentColor = Color.Black
                backgroundColor = Color.White
            }
            val user = Firebase.auth.currentUser
            var userName: String? by remember { mutableStateOf("") }
            var userEmail: String? by remember { mutableStateOf("") }
            var userId: String? by remember { mutableStateOf("") }
            var userImage: Uri? by remember { mutableStateOf(null) }
            user?.let {
                userName = it.displayName
                userEmail = it.email
                userId = it.uid
                userImage = it.photoUrl
            }
            GOATTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = backgroundColor
                ) {
                    //네비게이션 컴포즈를 위한 네비게이션 컨트롤러
                    val navController = rememberNavController()
                    //뒤로가기를 컨트롤하기 위한 명령어
                    var waitTime by remember { mutableStateOf(0L) }
                    BackHandler(enabled = true, onBack = {
                        if (System.currentTimeMillis() - waitTime >= 1600) {
                            waitTime = System.currentTimeMillis()
                            Toast.makeText(
                                this@MainActivity, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            finish()
                        }
                    })
                    //네비게이션 컴포즈 구현부
                    //trasition쪽이 애니메이션, composable이 각 화면들(액티비티)/화면은 함수로, route로 화면 state관리
                    //navigate로 화면 이동, popupto로 스택관리
                    NavHost(navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        }) {
                        composable("home") {
                            HomeScreen(
                                onNavigateToNext = {
                                    navController.navigate("next") {
                                        popUpTo("next") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToReservation = {
                                    navController.navigate("reservation") {
                                        popUpTo("reservation") {
                                            inclusive = true
                                        }
                                    }
                                },
                                contentColor = contentColor,
                                backgroundColor = backgroundColor,
                                switchAct = {
                                    isDark = !isDark
                                    sharedPref?.edit {
                                        putBoolean("Dark", isDark)
                                    }
                                },
                                isDark = isDark,
                                sharedPref = sharedPref,
                                onNavigateToChatting = {
                                    navController.navigate("chatting") {
                                        popUpTo("chatting") {
                                            inclusive = true
                                        }
                                    }
                                },
                                logOut = {
                                    val auth: FirebaseAuth = FirebaseAuth.getInstance()
                                    val myIntent = Intent(context, StartActivity::class.java)
                                    finish()
                                    context.startActivity(myIntent)
                                    auth.signOut()
                                },
                                userName = userName,
                                userEmail = userEmail,
                                userId = userId,
                                userImage = userImage
                            )
                        }
                        ////다음 페이지 관리////////////////
                        composable("next") {
                            NextScreen(backgroundColor = backgroundColor,
                                contentColor = contentColor,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                })
                        }
                        composable("reservation") {
                            ReservationScreen(contentColor = contentColor,
                                backgroundColor = backgroundColor,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                })
                        }
                        composable("chatting") {
                            Chatting()
                        }
                    }
                }
            }
        }
    }
}

//메인 페이지(홈 화면)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onNavigateToNext: () -> Unit,
    onNavigateToReservation: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    switchAct: (Boolean) -> Unit,
    isDark: Boolean,
    sharedPref: SharedPreferences?,
    onNavigateToChatting: () -> Unit,
    logOut: () -> Unit,
    userName:String?,
    userEmail:String?,
    userId:String?,
    userImage:Uri?
) {
    var userName: String? by remember { mutableStateOf(userName ?: "이름없음") }
    var userEmail: String? by remember { mutableStateOf(userEmail ?: "example@naver.com") }
    var userId: String? by remember { mutableStateOf(userId ?: null) }
    var userImage: Uri? by remember { mutableStateOf(userImage ?: null) }
    //다크 모드 설정 스위치 트리거
    var switchTrigger by remember { mutableStateOf(isDark) }
    //다크 모드 설정 애니메이션
    val animateBackgroundColor by animateColorAsState(
        targetValue = if (switchTrigger) Color(0xff121212) else Color.White,
        animationSpec = tween(800),
        label = "backgroundColor"
    )
    val animateContentColor: Color by animateColorAsState(
        targetValue = if (switchTrigger) Color.White else Color.Black,
        animationSpec = tween(1000),
        label = "contentColor"
    )
    //홈버튼, 메신저 버튼 체크
    var homeSelected by remember {
        val getHome = sharedPref?.getBoolean("Home", true) ?: true
        mutableStateOf(getHome)
    }
    //풀 화면 다이어로그 트리거 / 할 일 관리, 설정, 개인 프로필
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showSettingDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    // 서랍 상태 트리거
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 코루틴을 위한 코루틴 스코프
    val scope = rememberCoroutineScope()
    //해당 부분은 다이어로그 3개를 관리하는 명령어.
    if (showSettingDialog) {
        //설정 화면
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showSettingDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animateBackgroundColor)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = animateContentColor,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterVertically
                                )
                                .size(32.dp)
                                .clickable { showSettingDialog = false })
                        Spacer(modifier = Modifier.size(24.dp, 0.dp))
                        Text(
                            text = "환경설정",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = animateContentColor,
                            modifier = Modifier.align(
                                Alignment.CenterVertically
                            )
                        )
                    }
                    Divider(color = animateContentColor.copy(0.2f))
                    Spacer(modifier = Modifier.size(0.dp, 16.dp))
                    Column(modifier = Modifier.padding(32.dp, 4.dp)) {
                        Row {
                            Text(
                                text = "다크모드",
                                color = animateContentColor,
                                fontSize = 16.sp,
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                            Spacer(modifier = Modifier.size(24.dp, 0.dp))
                            Switch(
                                checked = switchTrigger, onCheckedChange = {
                                    switchTrigger = !switchTrigger
                                    switchAct(switchTrigger)
                                }, modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                    }
                }
            }
        }
    } else if (showScheduleDialog) {
        //할 일 관리 화면
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showScheduleDialog = false }) {
            ScheduleScreen(
                backgroundColor,
                contentColor,
                { showScheduleDialog = !showScheduleDialog },
                sharedPref = sharedPref
            )
        }
    } else if (showProfileDialog) {
        //개인 페이지 화면///////개인 프로필 깃발
        //여기에 로그인 정보로 꾸며야 함
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showProfileDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = contentColor,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterVertically
                                )
                                .size(32.dp)
                                .clickable { showProfileDialog = !showProfileDialog })
                        Spacer(modifier = Modifier.size(24.dp, 0.dp))
                        Text(
                            text = "프로필 변경",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = contentColor,
                            modifier = Modifier.align(
                                Alignment.CenterVertically
                            )
                        )
                    }
                    Divider(color = contentColor.copy(0.2f))
                    Spacer(modifier = Modifier.size(0.dp, 16.dp))
                }
            }
        }
    }
    //서랍에 대한 코드
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(0.6f),
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = backgroundColor, drawerContentColor = contentColor
            ) {
                Text(
                    text = "개인 페이지",
                    color = contentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(12.dp)
                )
                Divider(color = contentColor)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProfileDialog = !showProfileDialog }) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp, 30.dp)
                            .align(
                                Alignment.CenterStart
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "",
                            tint = contentColor,
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.size(16.dp, 1.dp))
                        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Row {
                                Text(
                                    text = "${userName}",
                                    fontSize = 24.sp,
                                    color = contentColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Text(
                                    text = "쌤",
                                    fontSize = 24.sp,
                                    color = contentColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            Spacer(modifier = Modifier.size(1.dp, 4.dp))
                            Text(text = "${userEmail}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
                NavigationDrawerItem(label = {
                    Text(
                        text = "▶  할 일 관리", fontWeight = FontWeight.Bold, color = contentColor
                    )
                }, selected = false, colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = backgroundColor,
                    unselectedContainerColor = backgroundColor,
                    selectedTextColor = contentColor,
                    unselectedTextColor = contentColor
                ), onClick = {
//                        scope.launch {
//                            drawerState.close()
//                        }
                    showScheduleDialog = !showScheduleDialog
                })
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(0.dp, 24.dp)
                    ) {
                        Row(modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable { showSettingDialog = !showSettingDialog }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Setting",
                                tint = contentColor,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(
                                        Alignment.CenterVertically
                                    )
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "설정",
                                fontSize = 16.sp,
                                color = contentColor,
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                        Spacer(modifier = Modifier.size(40.dp))
                        Divider(
                            color = contentColor,
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.size(40.dp))
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable(onClick = logOut)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ExitToApp,
                                contentDescription = "LogOut",
                                tint = contentColor,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(
                                        Alignment.CenterVertically
                                    )
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "로그아웃",
                                fontSize = 16.sp,
                                color = contentColor,
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                    }
                }
            }
        }) {
        //홈 화면에 대한 코드들
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                //맨 위의 앱 바
                CenterAlignedTopAppBar(title = {
                    Column {
                        Text(
                            text = "G.O.A.T",
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            ),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.size(1.dp))
                        Text(
                            text = "Groupware Of All Teachers",
                            fontSize = 12.sp,
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            ),
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDark) Color(0xff121212) else Color(0xFF4CAF50)
                ), navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(45.dp),
                            tint = Color.White
                        )
                    }
                })
                Divider()
                //앱 바 아래 화면
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(targetState = homeSelected, content = { homeSelected ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            ///해당 박스가 메인 화면 컨텐츠 구현부.
                            // 주의점은 Center가 Center가 아니라는것.
                            if (homeSelected) {
                                Column(modifier = Modifier.align(Alignment.Center)) {
                                    Button(
                                        onClick = onNavigateToNext,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(text = "다음 페이지")
                                    }
                                    Spacer(modifier = Modifier.size(30.dp))
                                    Button(
                                        onClick = onNavigateToReservation,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(text = "예약 페이지")
                                    }
                                }
                            } else {
                                ChatView(onNavigateToChatting = onNavigateToChatting)
                            }
                        }
                    }, label = "main content")
                    Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                        Divider(thickness = 1.dp, color = Color.Black.copy(0.1f))
                        //하단 바 명령어////////////////////////
                        NavigationBar(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            containerColor = backgroundColor
                        ) {
                            NavigationBarItem(selected = false,
                                onClick = {
                                    homeSelected = true
                                    sharedPref?.edit {
                                        putBoolean("Home", homeSelected)
                                    }
                                          },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Home,
                                        contentDescription = null,
                                        tint = if (homeSelected) Color(0xff2196f3) else if (switchTrigger) contentColor else LocalContentColor.current
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Home",
                                        color = if (homeSelected) Color(0xff2196f3) else if (switchTrigger) contentColor else LocalContentColor.current
                                    )
                                })
                            NavigationBarItem(selected = false,
                                onClick = {
                                    homeSelected = false
                                    sharedPref?.edit {
                                        putBoolean("Home", homeSelected)
                                    }
                                          },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.MailOutline,
                                        contentDescription = null,
                                        tint = if (homeSelected) if (switchTrigger) contentColor else LocalContentColor.current else Color(
                                            0xff2196f3
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Message",
                                        color = if (homeSelected) if (switchTrigger) contentColor else LocalContentColor.current else Color(
                                            0xff2196f3
                                        )
                                    )
                                })
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleScreen(
    backgroundColor: Color,
    contentColor: Color,
    exitScheduleDialog: () -> Unit,
    sharedPref: SharedPreferences?
) {
    var xIndex by remember {
        mutableStateOf(0)
    }
    var yIndex by remember {
        mutableStateOf(0)
    }
    val week = listOf("월", "화", "수", "목", "금")
    val subject = listOf(
        "수학1",
        "수학2",
        "국어1",
        "국어2",
        "영어",
        "물리1",
        "물리2",
        "기술가정1",
        "기술가정2",
        "화학1",
        "화학2",
        "지구과학1",
        "지구과학2",
        "생명과학1",
        "생명과학2",
        "한국사",
        "세계사",
        "일본어",
        "독일어",
        "스페인어",
        "중국어",
        "러시아어",
        "아랍어",
        "베트남어",
        "한문",
        "프랑스어",
        "생활과 윤리",
        "윤리와 사상",
        "한국 지리",
        "세계 지리",
        "동아시아사",
        "정치와 법",
        "경제",
        "사회문화"
    )
    var resetScheduleDataList by remember { mutableStateOf(false) }
    var scheduleDataList by remember(resetScheduleDataList) {
        mutableStateOf(
            Array(8,
                { Array(5, { ScheduleData(subject = "", memo = "", color = Color.Transparent) }) })
        )
    }
    if (resetScheduleDataList) {
        resetScheduleDataList != resetScheduleDataList
    }
    var showResetAlert by remember {
        mutableStateOf(false)
    }
    var showAddDialog by remember {
        mutableStateOf(false)
    }
    var searchKeyword by remember(showAddDialog) { mutableStateOf("") }
    var saveSubject by remember(showAddDialog) { mutableStateOf(if (scheduleDataList[yIndex][xIndex].subject != "") scheduleDataList[yIndex][xIndex].subject else "과목을 설정해주세요") }
    var typeMemo by remember(showAddDialog) { mutableStateOf(if (scheduleDataList[yIndex][xIndex].subject != "") scheduleDataList[yIndex][xIndex].memo else "") }
    var isDropDownMenu by remember { mutableStateOf(false) }
    var subjectColor by remember(showAddDialog) {
        mutableStateOf(
            if (scheduleDataList[yIndex][xIndex].color != Color.Transparent) scheduleDataList[yIndex][xIndex].color else Color(
                0xFFBBD0E9
            )
        )
    }
    if (searchKeyword != "") {
        isDropDownMenu = true
    }
    if (showAddDialog) {
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showAddDialog = false }) {
            var showColorEditDialog by remember {
                mutableStateOf(false)
            }
            val scheduleColorList = listOf(
                Color(0xFFBBD0E9),
                Color(0xFFC7B0C2),
                Color(0xFF8088B2),
                Color(0xFFB4C8BB),
                Color(0xFF3F568B),
                Color(0xFFD5D7D8),
                Color(0xFF778C86),
                Color(0xFFE7E3E3)
            )
            if (showColorEditDialog) {
                Dialog(onDismissRequest = { showColorEditDialog = false }) {
                    Box(
                        modifier = Modifier
                            .size(320.dp, 300.dp)
                            .clip(RoundedCornerShape(10))
                            .background(backgroundColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(0.dp, 16.dp)
                        ) {
                            Text(
                                text = "색상 선택",
                                fontSize = 20.sp,
                                color = contentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                )
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Divider(color = contentColor)
                            Spacer(modifier = Modifier.size(30.dp))
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.align(Alignment.Center)) {
                                    for (i in 0..1) {
                                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                            for (j in 0..3) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.CenterVertically)
                                                        .size(72.dp, 88.dp)
                                                        .background(scheduleColorList[j + i * 4])
                                                        .clickable {
                                                            subjectColor =
                                                                scheduleColorList[j + i * 4]
                                                            showColorEditDialog =
                                                                !showColorEditDialog
                                                        }
                                                        .border(
                                                            1.dp,
                                                            contentColor.copy(0.5f),
                                                            shape = RectangleShape
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(subjectColor)
                    .clickable { showColorEditDialog = !showColorEditDialog }) {
                    Text(
                        text = "${saveSubject}",
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(40.dp, 0.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "ColorChange",
                        tint = Color.Gray,
                        modifier = Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(16.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .align(
                            Alignment.TopCenter
                        )
                        .padding(top = 304.dp)
                ) {
                    TextField(
                        value = typeMemo,
                        onValueChange = {
                            typeMemo = it
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = contentColor,
                            disabledTextColor = Color.Gray,
                            unfocusedTextColor = contentColor,
                            focusedPlaceholderColor = Color.Gray,
                            disabledPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            cursorColor = contentColor
                        ),
                        placeholder = { Text(text = "메모 입력") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .padding(40.dp, 0.dp)
                            .border(
                                1.dp,
                                color = contentColor,
                                shape = RoundedCornerShape(10)
                            )
                    )
                    Spacer(modifier = Modifier.size(32.dp))
                    Button(
                        onClick = {
                            showAddDialog = false
                            scheduleDataList[yIndex][xIndex].subject = saveSubject
                            scheduleDataList[yIndex][xIndex].memo = typeMemo
                            scheduleDataList[yIndex][xIndex].color = subjectColor
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "추가하기")
                    }
                }
                Column(modifier = Modifier.align(Alignment.TopCenter)) {
                    Spacer(modifier = Modifier.size(176.dp))
                    Text(
                        text = "과목 검색",
                        color = contentColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(56.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    TextField(
                        value = searchKeyword,
                        onValueChange = {
                            searchKeyword = it
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = contentColor,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                            )
                        },
                        maxLines = 1,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = contentColor,
                            disabledTextColor = Color.Gray,
                            unfocusedTextColor = contentColor,
                            focusedPlaceholderColor = Color.Gray,
                            disabledPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            cursorColor = contentColor
                        ),
                        placeholder = { Text(text = "검색어 입력") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(24.dp, 0.dp)
                            .border(
                                BorderStroke(
                                    width = 2.dp,
                                    color = contentColor
                                ), shape = RoundedCornerShape(
                                    50
                                )
                            )
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    if (searchKeyword != "") {
                        subject.filter { it.contains(searchKeyword) }.forEach {
                            Box(modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(56.dp, 0.dp)
                                .background(backgroundColor)
                                .clickable {
                                    saveSubject = it
                                    searchKeyword = ""
                                }
                                .border(
                                    1.dp,
                                    color = contentColor.copy(0.2f),
                                    shape = RectangleShape
                                )) {
                                Text(
                                    text = it,
                                    fontSize = 16.sp,
                                    color = contentColor,
                                    modifier = Modifier
                                        .align(
                                            Alignment.Center
                                        )
                                        .padding(0.dp, 12.dp)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = "",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterEnd
                                        )
                                        .padding(16.dp, 0.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showResetAlert) {
        AlertDialog(
            onDismissRequest = { showResetAlert = false },
            title = { Text("초기화 하시겠습니까?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "초기화 후엔 다시 되돌릴 수 없습니다.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(260.dp, 30.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResetAlert = false
                    resetScheduleDataList = true
                }) {
                    Text("초기화", color = contentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAlert = false }) {
                    Text("취소", color = contentColor)
                }
            },
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            textContentColor = contentColor,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "경고",
                    tint = contentColor
                )
            },
            iconContentColor = contentColor
        )
    }
    var forRecomposition by remember(showAddDialog == false) {
        mutableStateOf("교")
    }
    var showCalendarDialog by remember() { mutableStateOf(false) }
    var currentDate by remember {
        val getDate = sharedPref?.getString("Date", "클릭으로 날짜 선택") ?: "클릭으로 날짜 선택"
        mutableStateOf(getDate)
    }
    if (showCalendarDialog) {
        val calendar = Calendar.getInstance()
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.ROOT)
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(onDismissRequest = {
            showCalendarDialog = false
        }, confirmButton = {
            TextButton(
                onClick = {
                    currentDate = formatter.format(Date(datePickerState.selectedDateMillis!!))
                    sharedPref?.edit {
                        putString("Date", currentDate)
                    }
                    showCalendarDialog = !showCalendarDialog
                }, enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showCalendarDialog = !showCalendarDialog
            }) {
                Text("Cancel")
            }
        }) {
            DatePicker(
                state = datePickerState
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = "back",
                    tint = contentColor,
                    modifier = Modifier
                        .align(
                            Alignment.CenterVertically
                        )
                        .size(32.dp)
                        .clickable(onClick = exitScheduleDialog)
                )
                Spacer(modifier = Modifier.size(24.dp, 0.dp))
                Text(
                    text = "할 일 관리",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = contentColor,
                    modifier = Modifier.align(
                        Alignment.CenterVertically
                    )
                )
            }
            Divider(color = contentColor.copy(0.2f))
            Spacer(modifier = Modifier.size(0.dp, 16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.align(Alignment.TopCenter)) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = "< ${currentDate} >",
                        color = contentColor,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .clickable { showCalendarDialog = !showCalendarDialog })
                    Spacer(modifier = Modifier.size(32.dp))
                    Box(
                        modifier = Modifier
                            .size(380.dp, 440.dp)// 565
                            .align(Alignment.CenterHorizontally)
                            .clip(
                                RoundedCornerShape(5)
                            )
                            .border(1.dp, contentColor, shape = RoundedCornerShape(5))
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.TopCenter)
                        ) {
                            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Box(modifier = Modifier
                                    .size(30.dp, 25.dp)
                                    .drawBehind { // 여기에 원하는 그리기 작업을 수행합니다.
                                        val topRight = Offset(size.width, 0f)
                                        val bottomLeft = Offset(0f, size.height)
                                        val bottomRight = Offset(size.width, size.height)
                                        drawLine(contentColor, topRight, bottomRight, 1f)
                                        drawLine(contentColor, bottomLeft, bottomRight, 1f)
                                    }) {
                                    Text(
                                        text = " ",
                                        color = contentColor,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                for (i in 0..4) {
                                    Box(modifier = Modifier
                                        .size(70.dp, 25.dp)
                                        .drawBehind { // 여기에 원하는 그리기 작업을 수행합니다.
                                            val topRight = Offset(size.width, 0f)
                                            val bottomLeft = Offset(0f, size.height)
                                            val bottomRight = Offset(size.width, size.height)
                                            drawLine(
                                                contentColor, topRight, bottomRight, 1f
                                            )
                                            drawLine(
                                                contentColor, bottomLeft, bottomRight, 1f
                                            )
                                        }) {
                                        Text(
                                            text = "${week[i]}",
                                            color = contentColor,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                            LazyColumn(state = rememberLazyListState()) {
                                items(8) { i ->
                                    Row {
                                        Box(modifier = Modifier
                                            .size(30.dp, 90.dp)
                                            .drawBehind { // 여기에 원하는 그리기 작업을 수행합니다.
                                                val topLeft = Offset(0f, 0f)
                                                val topRight = Offset(size.width, 0f)
                                                val bottomLeft = Offset(0f, size.height)
                                                val bottomRight = Offset(size.width, size.height)
                                                drawLine(
                                                    contentColor, topLeft, bottomLeft, 1f
                                                )
                                                drawLine(
                                                    contentColor, topRight, bottomRight, 1f
                                                )
                                                drawLine(
                                                    contentColor, bottomLeft, bottomRight, 1f
                                                )
                                            }) {
                                            Column(Modifier.align(Alignment.Center)) {
                                                Text(
                                                    text = "${i + 1}",
                                                    color = contentColor,
                                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                                )
                                                Text(
                                                    text = forRecomposition,
                                                    color = contentColor,
                                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                                )
                                                Text(
                                                    text = "시",
                                                    color = contentColor,
                                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                                )
                                            }
                                        }
                                        Row {
                                            for (j in 0..4) {
                                                Box(modifier = Modifier
                                                    .size(70.dp, 90.dp)
                                                    .background(scheduleDataList[i][j].color)
                                                    .clickable {
                                                        xIndex = j
                                                        yIndex = i
                                                        showAddDialog = !showAddDialog
                                                    }
                                                    .drawBehind { // 여기에 원하는 그리기 작업을 수행합니다.
                                                        val topRight = Offset(size.width, 0f)
                                                        val bottomLeft = Offset(0f, size.height)
                                                        val bottomRight =
                                                            Offset(size.width, size.height)
                                                        drawLine(
                                                            contentColor,
                                                            topRight,
                                                            bottomRight,
                                                            1f
                                                        )
                                                        drawLine(
                                                            contentColor,
                                                            bottomLeft,
                                                            bottomRight,
                                                            1f
                                                        )
                                                    }) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text(
                                                            text = scheduleDataList[i][j].subject,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                        Spacer(modifier = Modifier.size(4.dp))
                                                        Text(
                                                            text = scheduleDataList[i][j].memo,
                                                            fontSize = 12.sp,
                                                            color = Color.White,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(0.dp, 80.dp)
                ) {
                    Button(onClick = { showResetAlert = !showResetAlert }) {
                        Text(text = "초기화")
                    }
                }
            }
        }
    }
    //Box(modifier = Modifier.background(Color.Gray.copy(if(showCalendarDialog) 0.4f else 0f)))
}

//다음 페이지(기능)
@Composable
fun NextScreen(backgroundColor: Color, contentColor: Color, onNavigateToHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        //해당 부분 작성 요망
    }
}

@Composable
fun ReservationScreen(onNavigateToHome: () -> Unit, contentColor: Color, backgroundColor: Color) {
    val reservationPlaceList = listOf("체육관,강당", "잔디 운동장", "교실", "도서관")
    var state by remember { mutableStateOf(0) }
    val facilities = listOf(
        FacilityInfo(
            "체육관,강당",
            "700m²",
            "35명",
            "평일: 개방시간없음\n토요일: 개방시간없음",
            "주의 사항 : 주말에만 사용가능합니다.\n학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.ic_launcher_background//R.drawable.groundsil
        ),
        FacilityInfo(
            "잔디 운동장",
            "3988m²",
            "60명",
            "평일: 개방시간없음\n일요일: 개방시간없음",
            "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.ic_launcher_background//R.drawable.groundya
        ),
        FacilityInfo(
            "일반교실",
            "66m²",
            "25명",
            "평일: 개방시간없음",
            "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.ic_launcher_background//R.drawable.chair
        ),
        FacilityInfo(
            "도서관",
            "198m²",
            "60명",
            "평일: 개방시간없음",
            "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.ic_launcher_background//R.drawable.library
        ),
    )
    var useFacility by remember {
        mutableStateOf(
            FacilityInfo(
                "체육관,강당",
                "700m²",
                "35명",
                "평일: 개방시간없음\n토요일: 개방시간없음",
                "주의 사항 : 주말에만 사용가능합니다.\n학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
                R.drawable.ic_launcher_background//R.drawable.groundsil
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            TabRow(
                selectedTabIndex = state,
                containerColor = backgroundColor,
                contentColor = contentColor
            ) {
                reservationPlaceList.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = {
                            state = index
                            useFacility = facilities[index]
                        },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selectedContentColor = contentColor,
                        unselectedContentColor = Color.Gray
                    )
                }
            }
//            Row(modifier = Modifier.padding(16.dp)) {
//                Icon(
//                    imageVector = Icons.Outlined.KeyboardArrowLeft,
//                    contentDescription = "back",
//                    tint = contentColor,
//                    modifier = Modifier
//                        .align(
//                            Alignment.CenterVertically
//                        )
//                        .size(32.dp)
//                        .clickable(onClick = onNavigateToHome)
//                )
//                Spacer(modifier = Modifier.size(24.dp, 0.dp))
//                Text(
//                    text = "시설 예약",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    color = contentColor,
//                    modifier = Modifier.align(
//                        Alignment.CenterVertically
//                    )
//                )
//            }
//            Divider(color = contentColor)
            Spacer(modifier = Modifier.size(0.dp, 16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                ReservationAlert(contentColor, backgroundColor)
                ////////////////////
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp)
                ) {
                    Text(
                        text = "시설소개",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Add spacer to create some space
                    Text(
                        text = "시설명 : ${useFacility.name}\n" +
                                "면적: ${useFacility.area}\n" +
                                "수용인원 : ${useFacility.capacity}\n" +
                                "개방시간: ${useFacility.openingHours} \n평일: 개방시간없음\n" +
                                "토요일: 개방시간없음\n" +
                                "주의 사항 : ${useFacility.caution} 주말에만 사용가능합니다.\n" +
                                "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 \n" +
                                "예약 전 미리 연락주시기 바랍니다.",
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = contentColor
                    )
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background/*useFacility.image*/),
                            contentDescription = null,
                            modifier = Modifier
                                .size(240.dp)  // 이미지 크기 조절
                                .clip(shape = RoundedCornerShape(8.dp))// 이미지 모서리 둥글게
                                .padding(0.dp, 16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    Button(
                        onClick = onNavigateToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = "예약하기")
                    }
                }

                /////////////////////
            }
        }
    }
}

@Composable      // 첫 알림 메세지화면
fun ReservationAlert(contentColor: Color, backgroundColor: Color) {
    var showDialog by remember { mutableStateOf(true) } // 초기에 다이얼로그를 열도록 설정
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clip(RoundedCornerShape(10))
                    .background(backgroundColor)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "시설물 대관 공지사항", fontWeight = FontWeight.Bold, color = contentColor
                )    //안내문
                Spacer(modifier = Modifier.size(32.dp))
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                    ) {
                        Text(
                            text = " 익월 신규 예약오픈시 과도한 트래픽으로 예약신청이 정상적으로 진행되지 않을 수 있습니다.\n" + "또한 예약신청 후 일부 대관료 금액이 잘못 발송될 경우 반드시 " + "예약현황/취소에서 예약현황에 표시된 일정의 대관금액으로 " + "별도 안내드리며 대관료 및 입금자명이 다르게 잡았을시 취소처리가 될수 있으니 " + "반드시 예약현황/취소에서 예약현황에 표시된 해당 일정 대관금액으로 전용입금 계좌에 꼭 입금해 주시기 바랍니다.\n" + "이용 고객은 항시 꼭 확인 부탁드립니다.",
                            lineHeight = 24.sp,
                            color = contentColor,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(60.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.End // 체크박스를 우측에 배치
                ) {
                    Checkbox(
                        checked = !showDialog, onCheckedChange = {
                            showDialog = !showDialog
                        }, modifier = Modifier
                            .padding(end = 8.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        "다시 열지 않음",
                        color = contentColor,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun Chatting() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var messages = remember { mutableStateListOf<String>("") }
        var newMessage by remember { mutableStateOf("") }
        var displayedMessages by remember { mutableStateOf(emptyList<Message>()) }
        var time = remember { mutableStateListOf<String>() }
        val messageRef = remember { Firebase.database.getReference("messages").child("message") }
        // Write a message to the database
        LaunchedEffect(Unit) {
            messageRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val text = snapshot.child("text").getValue(String::class.java)
                    val sender = snapshot.child("sender").getValue(String::class.java)
                    val timestamp = snapshot.child("timestamp").getValue(Long::class.java)
                    if (text != null && sender != null && timestamp != null) {
                        val message = Message(text, sender, timestamp)
                        if (!displayedMessages.contains(message)) {
                            displayedMessages += message
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//            TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
//            TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//            TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        }
        // Create LazyListState to manage scrolling
        val scrollState = rememberLazyListState()
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f), // Expand to take remaining space
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Bottom,
                state = scrollState // Assign LazyListState to LazyColumn
            ) {
                // "yyyy-MM-dd HH:mm:ss"
                //Text(text = "Sender: ${message.user ?: ""}") // Display user ID
                items(displayedMessages) { message ->
                    val timestampShow =
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                            Date(message.timestamp as Long)
                        )
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(text = timestampShow, color = Color.Black, fontSize = 9.sp)
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.buttonColors(Color.LightGray),
                                shape = RoundedCornerShape(topStart = 25.dp, bottomStart = 5.dp),
                            ) {
                                message.text?.let {
                                    Text(
                                        text = it,
                                        textAlign = TextAlign.Start,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    shape = RectangleShape,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(text = "+")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            val messageData = mapOf(
                                "text" to newMessage,
                                "sender" to "너",
                                "timestamp" to ServerValue.TIMESTAMP
                            )
                            messageRef.push().setValue(messageData)
//                            messages.add(newMessage)
                            newMessage = "" // Clear the input
//                            time.add(ServerValue.TIMESTAMP.toString())
                        }
                    },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(text = "Send")
                }
            }
        }
        LaunchedEffect(displayedMessages.size) {
            scrollState.animateScrollToItem(displayedMessages.size)
        }
    }
}

// Message data class
data class Message(
    val text: String? = null,
    val sender: String = "",
    val timestamp: Any? = null
)

@Composable
fun ChatView(onNavigateToChatting: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        LazyColumn {
            items(10) { message ->
                RoomList(message = message, onNavigateToChatting = onNavigateToChatting)
            }
        }
    }
}

//메세지 창에 띄울 메세지 목록의 틀
@Composable
fun RoomList(message: Int, onNavigateToChatting: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            //메세지 목록 중 하나를 눌렀을 때 해당 목록의 메세지 창으로 전환
            .clickable(onClick = onNavigateToChatting),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RectangleShape,
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(15.dp))
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(20.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "단톡방 사진",
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.width(3.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(268.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "이름${message}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp),
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
        }
    }
}

data class FacilityInfo(
    val name: String,
    val area: String,
    val capacity: String,
    val openingHours: String,
    val caution: String,
    val image: Int
)

data class ScheduleData(
    var subject: String, var memo: String, var color: Color
)



