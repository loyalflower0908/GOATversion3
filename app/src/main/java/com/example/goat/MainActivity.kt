package com.example.goat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.goat.ui.theme.GOATTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.math.log


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            val attendanceManager = remember { AttendanceManager() }
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
            //홈버튼, 메신저 버튼 체크
            var homeSelected by remember { mutableStateOf(true) }
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
                                onNavigateToStudent = {
                                    navController.navigate("student") {
                                        popUpTo("student") {
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
                                onNavigateToChatting1 = {
                                    navController.navigate("chatting1") {
                                        popUpTo("chatting1") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToChatting2 = {
                                    navController.navigate("chatting2") {
                                        popUpTo("chatting2") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToChatting3 = {
                                    navController.navigate("chatting3") {
                                        popUpTo("chatting3") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToChattingAll = {
                                    navController.navigate("chattingAll") {
                                        popUpTo("chattingAll") {
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
                                userImage = userImage,
                                onNavigateToNotice = {
                                    navController.navigate("notice") {
                                        popUpTo("notice") {
                                            inclusive = true
                                        }
                                    }
                                },
                                db = db,
                                context = context,
                                homeSelected = homeSelected,
                                homeSelectTrue = {homeSelected = true},
                                homeSelectFalse = {homeSelected = false}
                            )
                        }
                        ////다음 페이지 관리////////////////
                        composable("student") {
                            StudentScreen(
                                backgroundColor = backgroundColor,
                                contentColor = contentColor,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                },
                                db = db,
                                navController = navController,
                                attendanceManager = attendanceManager
                            )
                        }
                        composable(
                            "details/{studentName}",
                            arguments = listOf(navArgument("studentName") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val studentName = backStackEntry.arguments?.getString("studentName")
                            studentName?.let { name ->
                                DetailsPage(
                                    studentName = name,
                                    attendanceManager = attendanceManager,
                                    onSave = {
                                        navController.popBackStack() // 저장 완료 후 뒤로 가기
                                    },
                                    onBack = {
                                        navController.popBackStack() // 뒤로 가기
                                    },
                                    onDelete = {
                                        deleteStudent(name) // 학생 삭제
                                        navController.popBackStack() // 삭제 후 뒤로 가기
                                    },
                                    backgroundColor = backgroundColor,
                                    contentColor = contentColor
                                )
                            }
                        }
                        composable("notice") {
                            NoticeScreen(
                                backgroundColor = backgroundColor,
                                contentColor = contentColor,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                },
                                user = user,
                                context = context
                            )
                        }
                        composable("reservation") {
                            ReservationScreen(
                                contentColor = contentColor,
                                backgroundColor = backgroundColor,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                },
                                sharedPref = sharedPref
                            )
                        }
                        composable("chatting1") {
                            Chatting1(user, backgroundColor, contentColor, onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            })
                        }
                        composable("chatting2") {
                            Chatting2(user, backgroundColor, contentColor, onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            })
                        }
                        composable("chatting3") {
                            Chatting3(user, backgroundColor, contentColor, onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            })
                        }
                        composable("chattingAll") {
                            ChattingAll(user, backgroundColor, contentColor, onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            })
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
    onNavigateToStudent: () -> Unit,
    onNavigateToReservation: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    switchAct: (Boolean) -> Unit,
    isDark: Boolean,
    sharedPref: SharedPreferences?,
    onNavigateToChatting1: () -> Unit,
    onNavigateToChatting2: () -> Unit,
    onNavigateToChatting3: () -> Unit,
    onNavigateToChattingAll: () -> Unit,
    logOut: () -> Unit,
    userName: String?,
    userEmail: String?,
    userId: String?,
    userImage: Uri?,
    onNavigateToNotice: () -> Unit,
    db: FirebaseFirestore,
    context: Context,
    homeSelected: Boolean,
    homeSelectTrue:() -> Unit,
    homeSelectFalse:() -> Unit
) {
    val userName: String? by remember { mutableStateOf(userName ?: "이름없음") }
    val userEmail: String? by remember { mutableStateOf(userEmail ?: "example@naver.com") }
    val userId: String? by remember { mutableStateOf(userId ?: null) }
    val userImage: Uri? by remember { mutableStateOf(userImage ?: null) }
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
                db = db,
                userId = userId
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
                    TeacherDetailScreen(sharedPref = sharedPref, context = context, backgroundColor = backgroundColor, contentColor = contentColor, logOut = logOut)
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
                        if(userImage != null){
                            Image(
                                painter = rememberImagePainter(data = userImage),
                                contentDescription = "User Profile Image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterVertically)
                            )
                        }else{
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "",
                                tint = contentColor,
                                modifier = Modifier
                                    .size(80.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
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
                    if(userImage != null){
                        Image(painter = rememberImagePainter(data = userImage),
                            contentDescription = "",
                            modifier = Modifier
                                .size(45.dp)
                                .clip(shape = RoundedCornerShape(50))
                                .clickable { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }
                        )
                    }else{
                        IconButton(onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(45.dp),
                                tint = Color.White
                            )
                        }
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
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(136.dp)
                                            .clip(
                                                RoundedCornerShape(50)
                                            )
                                            .background(backgroundColor)
                                            .clickable(onClick = onNavigateToNotice)
                                            .border(
                                                width = 4.dp,
                                                color = contentColor,
                                                shape = RoundedCornerShape(50)
                                            )
                                    ) {
                                        Column(modifier = Modifier.align(Alignment.Center)) {
                                            Icon(
                                                imageVector = Icons.Filled.Notifications,
                                                contentDescription = "notice",
                                                tint = contentColor,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.size(4.dp))
                                            Text(
                                                text = "공지사항",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = contentColor,
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(60.dp))
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(136.dp)
                                            .clip(
                                                RoundedCornerShape(50)
                                            )
                                            .background(backgroundColor)
                                            .clickable(onClick = onNavigateToStudent)
                                            .border(
                                                width = 4.dp,
                                                color = contentColor,
                                                shape = RoundedCornerShape(50)
                                            )
                                    ) {
                                        Column(modifier = Modifier.align(Alignment.Center)) {
                                            Icon(
                                                imageVector = Icons.Filled.Face,
                                                contentDescription = "student",
                                                tint = contentColor,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.size(4.dp))
                                            Text(
                                                text = "학생 출결",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = contentColor,
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(60.dp))
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(136.dp)
                                            .clip(
                                                RoundedCornerShape(50)
                                            )
                                            .background(backgroundColor)
                                            .clickable(onClick = onNavigateToReservation)
                                            .border(
                                                width = 4.dp,
                                                color = contentColor,
                                                shape = RoundedCornerShape(50)
                                            )
                                    ) {
                                        Column(modifier = Modifier.align(Alignment.Center)) {
                                            Icon(
                                                imageVector = Icons.Filled.DateRange,
                                                contentDescription = "reservation",
                                                tint = contentColor,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.size(4.dp))
                                            Text(
                                                text = "시설 예약",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = contentColor,
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(60.dp))
                                }
                            } else {
                                ChatView(
                                    onNavigateToChatting1 = onNavigateToChatting1,
                                    onNavigateToChatting2 = onNavigateToChatting2,
                                    onNavigateToChatting3 = onNavigateToChatting3,
                                    onNavigateToChattingAll = onNavigateToChattingAll,
                                    backgroundColor = backgroundColor,
                                    contentColor = contentColor
                                )
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
                                onClick = homeSelectTrue,
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
                                onClick = homeSelectFalse,
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

@Composable
fun TeacherDetailScreen(sharedPref: SharedPreferences?, context: Context, backgroundColor: Color, contentColor: Color, logOut: () -> Unit) {
    var showLogOutAlert by remember { mutableStateOf(false) }
    if (showLogOutAlert){
        AlertDialog(
            onDismissRequest = { showLogOutAlert = false },
            title = { Text("로그아웃 하시겠습니까?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "변경된 이름이 적용되려면 다시 로그인해야합니다.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(260.dp, 68.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = logOut) {
                    Text("로그아웃", color = contentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogOutAlert = false }) {
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
    val user = Firebase.auth.currentUser
    var userName: String? by remember { mutableStateOf("") }
    user?.let {
        userName = it.displayName
    }
    var name by remember {
        val name = sharedPref?.getString("name", "") ?: ""
        mutableStateOf(name)
    }
    var a by remember {
        val a1 = sharedPref?.getString("a1", "") ?: ""
        mutableStateOf(a1)
    }
    var b by remember {
        val b1 = sharedPref?.getString("b1", "") ?: ""
        mutableStateOf(b1)
    }
    var c by remember {
        val c1 = sharedPref?.getString("c1", "") ?: ""
        mutableStateOf(c1)
    }
    var d by remember {
        val d1 = sharedPref?.getString("d1", "") ?: ""
        mutableStateOf(d1)
    }
    val onSaveClick: () -> Unit = {
        sharedPref?.edit()?.putString("name", name)?.apply()
        sharedPref?.edit()?.putString("a1", a)?.apply()
        sharedPref?.edit()?.putString("b1", b)?.apply()
        sharedPref?.edit()?.putString("c1", c)?.apply()
        sharedPref?.edit()?.putString("d1", d)?.apply()
        Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
        showLogOutAlert = !showLogOutAlert
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            OutlinedTextField(
                modifier= Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text("이름", color = contentColor) },
                maxLines = 1,
                colors = MyOutlineTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
            )
        }
        Spacer(modifier = Modifier.padding(32.dp))
        Box {
            Column {

                OutlinedTextField(
                    modifier= Modifier.fillMaxWidth(),
                    value = a,
                    onValueChange = { a = it },
                    label = { Text("번호", color = contentColor) },
                    maxLines = 1,
                    colors = MyOutlineTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                )


                OutlinedTextField(
                    modifier= Modifier.fillMaxWidth(),
                    value = b,
                    onValueChange = { b = it },
                    label = { Text("직책", color = contentColor) },
                    maxLines = 1,
                    colors = MyOutlineTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                )
                OutlinedTextField(
                    modifier= Modifier.fillMaxWidth(),
                    value = c,
                    onValueChange = { c = it },
                    label = { Text("담당부서", color = contentColor) },
                    maxLines = 1,
                    colors = MyOutlineTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                )
                OutlinedTextField(
                    modifier= Modifier.fillMaxWidth(),
                    value = d,
                    onValueChange = { d = it },
                    label = { Text("위치", color = contentColor) },
                    maxLines = 1,
                    colors = MyOutlineTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                )


                Spacer(modifier = Modifier.padding(32.dp))
                Button(
                    onClick = onSaveClick,
                    colors = ButtonDefaults.buttonColors(contentColor),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                ) {
                    Text(text = "저장", color = backgroundColor)
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
    db: FirebaseFirestore,
    userId: String?
) {
    var launchedRecomposition by remember {
        mutableStateOf(false)
    }
    var xIndex by remember {
        mutableStateOf(0)
    }
    var yIndex by remember {
        mutableStateOf(0)
    }
    val week = listOf("월", "화", "수", "목", "금")
    var resetScheduleDataList by remember { mutableStateOf(false) }
    val emptyScheduleDataList =
        Array(8, { Array(5, { ScheduleData(subject = "", memo = "", color = 0x00000000) }) })
    var scheduleDataList by remember(resetScheduleDataList) {
        mutableStateOf(
            Array(8,
                { Array(5, { ScheduleData(subject = "", memo = "", color = 0x00000000) }) })
        )
    }
    var currentDate by remember {
        val currentDateTime = LocalDateTime.now()
        val currentDay =
            "${currentDateTime.year}년 ${if (currentDateTime.monthValue < 10) "0" + currentDateTime.monthValue else currentDateTime.monthValue}월 ${currentDateTime.dayOfMonth}일"
        mutableStateOf(currentDay)
    }
    var previousDay by remember {
        mutableStateOf("")
    }
    var scheduleFireStoreMap: HashMap<String, Any> by remember {
        mutableStateOf(hashMapOf("날짜" to currentDate))
    }
    for (y in 0..7) {
        for (x in 0..4) {
            scheduleFireStoreMap.put("${y}${x}subject", scheduleDataList[y][x].subject)
            scheduleFireStoreMap.put("${y}${x}memo", scheduleDataList[y][x].memo)
            scheduleFireStoreMap.put("${y}${x}color", scheduleDataList[y][x].color)
        }
    }
    LaunchedEffect(true) {
        val scheduleDoc = db.collection("schedule").document(currentDate + userId).get().await()
        for (y in 0..7) {
            for (x in 0..4) {
                scheduleDataList[y][x].subject = scheduleDoc.getString("${y}${x}subject") ?: ""
                scheduleDataList[y][x].memo = scheduleDoc.getString("${y}${x}memo") ?: ""
                scheduleDataList[y][x].color = scheduleDoc.getLong("${y}${x}color") ?: 0x00000000
            }
        }
        previousDay = currentDate
        launchedRecomposition = true
    }
    if (previousDay != "") {
        LaunchedEffect(currentDate != previousDay) {
            val scheduleDoc = db.collection("schedule").document(currentDate + userId).get().await()
            for (y in 0..7) {
                for (x in 0..4) {
                    scheduleDataList[y][x].subject = scheduleDoc.getString("${y}${x}subject") ?: ""
                    scheduleDataList[y][x].memo = scheduleDoc.getString("${y}${x}memo") ?: ""
                    scheduleDataList[y][x].color =
                        scheduleDoc.getLong("${y}${x}color") ?: 0x00000000
                }
            }
            previousDay = currentDate
            launchedRecomposition = true
        }
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
            if (scheduleDataList[yIndex][xIndex].color.toInt() != 0x00000000) scheduleDataList[yIndex][xIndex].color else 0xFFBBD0E9
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
                0xFFBBD0E9,
                0xFFC7B0C2,
                0xFF8088B2,
                0xFFB4C8BB,
                0xFF3F568B,
                0xFFD5D7D8,
                0xFF778C86,
                0xFFE7E3E3
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
                                                        .background(Color(scheduleColorList[j + i * 4]))
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
                    .background(Color(subjectColor))
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
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor),
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
                            scheduleFireStoreMap.put(
                                "${yIndex}${xIndex}subject",
                                scheduleDataList[yIndex][xIndex].subject
                            )
                            scheduleFireStoreMap.put(
                                "${yIndex}${xIndex}memo",
                                scheduleDataList[yIndex][xIndex].memo
                            )
                            scheduleFireStoreMap.put(
                                "${yIndex}${xIndex}color",
                                scheduleDataList[yIndex][xIndex].color
                            )
                            db.collection("schedule")
                                .document(currentDate + userId)
                                .set(scheduleFireStoreMap)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot added with ID: ${documentReference}"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        },
                        colors = ButtonDefaults.buttonColors(contentColor),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "추가하기", color = backgroundColor)
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
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor),
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
                    for (y in 0..7) {
                        for (x in 0..4) {
                            scheduleFireStoreMap.put(
                                "${y}${x}subject",
                                emptyScheduleDataList[y][x].subject
                            )
                            scheduleFireStoreMap.put(
                                "${y}${x}memo",
                                emptyScheduleDataList[y][x].memo
                            )
                            scheduleFireStoreMap.put(
                                "${y}${x}color",
                                emptyScheduleDataList[y][x].color
                            )
                        }
                    }
                    db.collection("schedule")
                        .document(currentDate + userId)
                        .set(scheduleFireStoreMap)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
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
    var forStartRecomposition by remember(launchedRecomposition == true) {
        launchedRecomposition = false
        mutableStateOf("시")
    }
    var showCalendarDialog by remember() { mutableStateOf(false) }
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
                                                    text = forStartRecomposition,
                                                    color = contentColor,
                                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                                )
                                            }
                                        }
                                        Row {
                                            for (j in 0..4) {
                                                Box(modifier = Modifier
                                                    .size(70.dp, 90.dp)
                                                    .background(Color(scheduleDataList[i][j].color))
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
                    Button(
                        onClick = { showResetAlert = !showResetAlert },
                        colors = ButtonDefaults.buttonColors(contentColor)
                    ) {
                        Text(text = "초기화", color = backgroundColor)
                    }
                }
            }
        }
    }
    //Box(modifier = Modifier.background(Color.Gray.copy(if(showCalendarDialog) 0.4f else 0f)))
}

//다음 페이지(기능)
@Composable
fun StudentScreen(
    backgroundColor: Color,
    contentColor: Color,
    onNavigateToHome: () -> Unit,
    db: FirebaseFirestore,
    navController: NavHostController,
    attendanceManager: AttendanceManager
) {
    var changeFlag by remember { mutableStateOf(0) }
    var studentName by remember { mutableStateOf("") }
    var studentProfile by remember { mutableStateOf("") }
    val buttonText = remember { mutableStateOf("") } // 학생 이름을 표시할 버튼의 텍스트
    // 파이어베이스에서 데이터를 읽어옴
    val students = remember { mutableStateListOf<Student>() }
    LaunchedEffect(changeFlag) {
        val collectionRef = Firebase.firestore.collection("users")
        collectionRef.get().addOnSuccessListener { snapshot ->
            val newList = mutableListOf<Student>()
            for (document in snapshot.documents) {
                val studentDocument = document.data
                val student = Student(
                    name = document.id,
                    gradeAndClass = studentDocument?.get("gradeAndClass") as? String ?: "",
                    profile = studentDocument?.get("profile") as? String ?: "",
                    examGrade = studentDocument?.get("examGrade") as? String ?: "",
                    assignmentGrade = studentDocument?.get("assignmentGrade") as? String ?: "",
                    behavior = studentDocument?.get("behavior") as? String ?: "",
                    attitude = studentDocument?.get("attitude") as? String ?: "",
                    specialNote = studentDocument?.get("specialNote") as? String ?: "",
                    status = AttendanceStatus.ATTENDANCE,
                )
                newList.add(student)
            }
            students.clear()
            students.addAll(newList)
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
                        .clickable(onClick = onNavigateToHome)
                )
                Spacer(modifier = Modifier.size(24.dp, 0.dp))
                Text(
                    text = "학생 출석부",
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    TextFieldWithButton(
                        value = studentName,
                        onValueChange = { studentName = it },
                        onAddStudent = {
                            if (studentName.isNotBlank()) {
                                db.collection("users")
                                    .document(studentName)
                                    .set(hashMapOf<String, String>())
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }
                                changeFlag++
                                studentName = ""
                                studentProfile = ""
                            }
                        },
                        backgroundColor = backgroundColor,
                        contentColor = contentColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "학생 출결 목록",
                        fontSize = 20.sp,
                        color = contentColor
                    )
                    AttendanceList(
                        attendanceManager,
                        navController,
                        students,
                        backgroundColor,
                        contentColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    backgroundColor: Color,
    contentColor: Color,
    onNavigateToHome: () -> Unit,
    user: FirebaseUser?,
    context: Context
) {
    var newMessage by remember { mutableStateOf("") }
    var displayedNotice by remember { mutableStateOf(emptyList<Notice>()) }
    var showWriteDialog by remember { mutableStateOf(false) }
    val noticeRef = remember { Firebase.database.getReference("notice").child("전 학년") }

    user?.uid
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

    // Write a message to the database
    LaunchedEffect(Unit) {
        noticeRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val text = snapshot.child("text").getValue(String::class.java)
                val userId = snapshot.child("userId").getValue(String::class.java)
                val userName = snapshot.child("userName").getValue(String::class.java)
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java)
                val userImage = snapshot.child("userImage").getValue(String::class.java)

                if (text != null && userId != null && userName != null && timestamp != null && userImage != null) {
                    val notice = Notice(text, userId, userName, timestamp, userImage)

                    if (!displayedNotice.contains(notice)) {
                        displayedNotice += notice
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
    if (showWriteDialog) {
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showWriteDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    ) {
                        Row(modifier = Modifier.align(Alignment.TopStart)) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowLeft,
                                contentDescription = "back",
                                tint = contentColor,
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .size(32.dp)
                                    .clickable { showWriteDialog = false }
                            )
                            Spacer(modifier = Modifier.size(24.dp, 0.dp))
                            Text(
                                text = "작성 페이지",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = contentColor,
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                        Button(
                            onClick = {
                                if (newMessage.isNotBlank()) {
                                    val messageData = mapOf(
                                        "text" to newMessage,
                                        "userId" to userId,
                                        "userName" to userName,
                                        "timestamp" to ServerValue.TIMESTAMP,
                                        "userImage" to userImage.toString()
                                    )
                                    noticeRef.push().setValue(messageData)
                                    newMessage = ""
                                    showWriteDialog = false
                                } else {
                                    Toast.makeText(context, "오류 메시지: 내용이 없습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(contentColor),
                            shape = RoundedCornerShape(20),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(64.dp, 64.dp)
                                .align(Alignment.TopEnd)
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "글쓰기",
                                fontWeight = FontWeight.Bold,
                                color = backgroundColor
                            )
                        }
                    }
                    Divider(color = contentColor.copy(0.2f))
                    Spacer(modifier = Modifier.size(0.dp, 16.dp))
                    Box(modifier = Modifier.fillMaxSize()) {
                        TextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor), placeholder = { Text(text = "내용 입력") },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(600.dp)
                                .padding(40.dp, 0.dp)
                                .border(
                                    1.dp,
                                    color = contentColor,
                                    shape = RoundedCornerShape(10)
                                )
                        )
                    }
                }
            }
        }
    }
    // Create LazyListState to manage scrolling
    val scrollState = rememberLazyListState()
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
                        .clickable(onClick = onNavigateToHome)
                )
                Spacer(modifier = Modifier.size(24.dp, 0.dp))
                Text(
                    text = "공지사항",
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
            Column(Modifier.fillMaxSize()) {
                LazyColumn(
                    reverseLayout = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f), // Expand to take remaining space
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Top,
                    state = scrollState // Assign LazyListState to LazyColumn
                ) {
                    items(displayedNotice) { message ->
                        val isCurrentUserMessage = (userId == message.userId)
                        val timestampShow =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                                Date(message.timestamp as Long)
                            )

                        var image = message.userImage?.toUri()
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 여기에 uri값 받아서 이미지 출력하고 싶음
                                Spacer(modifier = Modifier.size(8.dp, 0.dp))
                                AsyncImage(
                                    model = image,
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.clip(
                                        RoundedCornerShape(30)
                                    )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(verticalArrangement = Arrangement.Top) {
                                    if (isCurrentUserMessage) {
                                        Text(
                                            text = "$userName",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = contentColor
                                        )
                                    } else {
                                        Text(
                                            text = "${message.userName}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = contentColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = timestampShow,
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            Button(
                                onClick = {
                                },
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                shape = RoundedCornerShape(10),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                                    .padding(8.dp)
                                    .border(
                                        width = 1.dp,
                                        color = contentColor,
                                        shape = RoundedCornerShape(10)
                                    )
                            ) {
                                message.text?.let {
                                    Text(
                                        text = it,
                                        textAlign = TextAlign.Start,
                                        color = contentColor,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            LaunchedEffect(displayedNotice.size) {
                scrollState.animateScrollToItem(displayedNotice.size)
            }
            //버튼
        }
        FloatingActionButton(
            onClick = { showWriteDialog = !showWriteDialog },
            containerColor = contentColor,
            contentColor = backgroundColor,
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(36.dp)
                .size(64.dp)

        ) {
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "write",
                tint = backgroundColor
            )
        }
    }
}


@Composable
fun ReservationScreen(
    onNavigateToHome: () -> Unit,
    contentColor: Color,
    backgroundColor: Color,
    sharedPref: SharedPreferences?
) {
    var showReservationDialog by remember { mutableStateOf(false) }
    val reservationPlaceList = listOf("체육관,강당", "잔디 운동장", "교실", "도서관")
    var state by remember { mutableStateOf(0) }
    val facilities = listOf(
        FacilityInfo(
            "체육관,강당",
            "700m²",
            "35명",
            "평일: 개방시간없음\n토요일: 개방시간없음",
            "주의 사항 : 주말에만 사용가능합니다.\n학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.groundsil
        ),
        FacilityInfo(
            "잔디 운동장",
            "3988m²",
            "60명",
            "평일: 개방시간없음\n일요일: 개방시간없음",
            "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.groundya
        ),
        FacilityInfo(
            "일반교실",
            "66m²",
            "25명",
            "평일: 개방시간없음",
            "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.chair
        ),
        FacilityInfo(
            "도서관",
            "198m²",
            "60명",
            "평일: 개방시간없음",
            "학사일정 및 학교사정에 따라 사용이 불가할 수 있으니 예약 전 미리 연락주시기 바랍니다.",
            R.drawable.library
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
                R.drawable.groundsil
            )
        )
    }
    if(showReservationDialog){
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false), onDismissRequest = { showReservationDialog = false }) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)){
                CalendarApp(contentColor = contentColor, backgroundColor = backgroundColor)
            }
        }
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
            Spacer(modifier = Modifier.size(0.dp, 16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                ReservationAlert(contentColor, backgroundColor, sharedPref)
                ////////////////////
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp)
                ) {
                    Spacer(modifier = Modifier.size(32.dp))
                    Text(
                        text = "시설소개",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "시설명 : ${useFacility.name}\n" + "면적: ${useFacility.area}\n" + "수용인원 : ${useFacility.capacity} \n" + "개방시간: ${useFacility.openingHours} \n" + "주의 사항 : ${useFacility.caution} \n",
                        fontSize = 15.sp,
                        lineHeight = 28.sp,
                        color = contentColor
                    )
                    Image(
                        painter = painterResource(id = useFacility.image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(300.dp)  // 이미지 크기 조절
                            .clip(shape = RoundedCornerShape(10))// 이미지 모서리 둥글게
                            .padding(0.dp, 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Button(
                        onClick = {showReservationDialog = !showReservationDialog},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(contentColor)
                    ) {
                        Text(text = "예약하기", color = backgroundColor)
                    }
                }

                /////////////////////
            }
        }
    }
}

@Composable      // 첫 알림 메세지화면
fun ReservationAlert(contentColor: Color, backgroundColor: Color, sharedPref: SharedPreferences?) {
    var showDialog by remember {
        val getAlert = sharedPref?.getBoolean("alert", true) ?: true
        mutableStateOf(getAlert)
    } // 초기에 다이얼로그를 열도록 설정
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
                            sharedPref?.edit {
                                putBoolean("alert", false)
                            }
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TextFieldWithButton(
    value: String,
    onValueChange: (String) -> Unit,
    onAddStudent: () -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {
    var keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = "학생 이름 입력") },
            modifier = Modifier
                .weight(1f)
                .size(300.dp, 60.dp)
                .clip(RoundedCornerShape(10))
                .border(width = 1.dp, color = contentColor, RoundedCornerShape(10)),
            maxLines = 1 ,
            colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onAddStudent()
                keyboardController?.hide()
            })
        )
        Spacer(modifier = Modifier.size(16.dp, 0.dp))
        Button(
            onClick = onAddStudent,
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(contentColor)
        ) {
            Text(text = "추가", color = backgroundColor)
        }
    }
}

@Composable
fun AttendanceList(
    attendanceManager: AttendanceManager,
    navController: NavHostController,
    students: List<Student>,
    backgroundColor: Color,
    contentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        students.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        navController.navigate("details/${student.name}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .wrapContentWidth(Alignment.Start)
                ) {
                    Text(text = student.name, color = contentColor)
                }

                val selectedStatus = remember(student.name) { mutableStateOf(student.status) }

                Repeat(3) { index ->
                    val newStatus = when (index) {
                        0 -> AttendanceStatus.ATTENDANCE
                        1 -> AttendanceStatus.LATE
                        else -> AttendanceStatus.ABSENCE
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Checkbox(
                            checked = selectedStatus.value == newStatus,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedStatus.value = newStatus
                                    attendanceManager.updateAttendance(student.name, newStatus)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = backgroundColor,
                                checkedColor = contentColor,
                                uncheckedColor = contentColor
                            ),
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Text(
                            text = when (newStatus) {
                                AttendanceStatus.ATTENDANCE -> "출석"
                                AttendanceStatus.LATE -> "지각"
                                AttendanceStatus.ABSENCE -> "결석"
                            }, color = contentColor
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    studentName: String,
    attendanceManager: AttendanceManager,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {


    val db = Firebase.firestore


//    val student = attendanceManager.getStudentByName(studentName)
//    var student: Student? by remember { mutableStateOf(null) }

    var profileState by remember { mutableStateOf("") }
    var examGradeState by remember { mutableStateOf("") }
    var assignmentGradeState by remember { mutableStateOf("") }
    var behaviorState by remember { mutableStateOf("") }
    var attitudeState by remember { mutableStateOf("") }
    var specialNoteState by remember { mutableStateOf("") }

    LaunchedEffect(studentName) {
//        if (student != null) {
        val studentDocument = db.collection("users").document(studentName).get().await()
//        student = Student(
//            name = studentName,
//            profile = studentDocument.getString("profile") ?: "",
//            examGrade = studentDocument.getString("examGrade") ?: "",
//            assignmentGrade = studentDocument.getString("assignmentGrade") ?: "",
//            behavior = studentDocument.getString("behavior") ?: "",
//            attitude = studentDocument.getString("attitude") ?: "",
//            specialNote = studentDocument.getString("specialNote") ?: "",
//        )
        profileState = studentDocument.getString("profile") ?: ""
        examGradeState = studentDocument.getString("examGrade") ?: ""
        assignmentGradeState = studentDocument.getString("assignmentGrade") ?: ""
        behaviorState = studentDocument.getString("behavior") ?: ""
        attitudeState = studentDocument.getString("attitude") ?: ""
        specialNoteState = studentDocument.getString("specialNote") ?: ""
//        }
    }

//    if (student != null) {
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
                        .clickable(onClick = onBack)
                )
                Spacer(modifier = Modifier.size(24.dp, 0.dp))
                Text(
                    text = "${studentName} 학생 상세정보",
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // 학생 프로필 표시 부분
                    Spacer(modifier = Modifier.size(32.dp))
                    Text(
                        text = "<  학생 이름: ${studentName}  >",
                        fontSize = 20.sp,
                        color = contentColor,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.size(32.dp))
                    // ... 다른 정보 표시

                    // 프로필 정보 입력 및 수정
                    TextField(
                        value = profileState,
                        onValueChange = { profileState = it },
                        label = { Text("프로필", color = contentColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = contentColor, shape = RoundedCornerShape(10)),
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    TextField(
                        value = examGradeState,
                        onValueChange = { examGradeState = it },
                        label = { Text("시험 성적", color = contentColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = contentColor, shape = RoundedCornerShape(10)),
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    TextField(
                        value = assignmentGradeState,
                        onValueChange = { assignmentGradeState = it },
                        label = { Text("과제 성적", color = contentColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = contentColor, shape = RoundedCornerShape(10)),
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    TextField(
                        value = behaviorState,
                        onValueChange = { behaviorState = it },
                        label = { Text("행동", color = contentColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = contentColor, shape = RoundedCornerShape(10)),
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    TextField(
                        value = attitudeState,
                        onValueChange = { attitudeState = it },
                        label = { Text("태도", color = contentColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = contentColor, shape = RoundedCornerShape(10)),
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    TextField(
                        value = specialNoteState,
                        onValueChange = { specialNoteState = it },
                        label = { Text("특이사항", color = contentColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = contentColor, shape = RoundedCornerShape(10)),
                        colors = MyTextFieldColor(contentColor = contentColor, backgroundColor = backgroundColor)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val studentInfo = hashMapOf(
                                    "profile" to profileState,
                                    "examGrade" to examGradeState,
                                    "assignmentGrade" to assignmentGradeState,
                                    "behavior" to behaviorState,
                                    "attitude" to attitudeState,
                                    "specialNote" to specialNoteState
                                )

                                db.collection("users")
                                    .document(studentName)
                                    .set(studentInfo)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        onSave() // 저장 완료 후 실행되는 함수 호출
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }
                            },
                            colors = ButtonDefaults.buttonColors(contentColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "저장", color = backgroundColor)
                        }
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(contentColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "삭제", color = backgroundColor)
                        }
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(contentColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "뒤로 가기", color = backgroundColor)
                        }
                    }
                }
            }
        }
    }
}

private fun deleteStudent(studentName: String) {
    val db = Firebase.firestore
    db.collection("users")
        .document(studentName)
        .delete()
        .addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot successfully deleted!")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error deleting document", e)
            // 실패 시 에러 메시지 표시 등
        }
}


@Composable
fun Repeat(times: Int, content: @Composable (Int) -> Unit) {
    for (i in 0 until times) {
        content(i)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextFieldColor(contentColor: Color, backgroundColor: Color):TextFieldColors{
    return TextFieldDefaults.textFieldColors(
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOutlineTextFieldColor(backgroundColor: Color, contentColor: Color):TextFieldColors{
    return OutlinedTextFieldDefaults.colors(focusedTextColor = contentColor,
        disabledTextColor = Color.Gray,
        unfocusedTextColor = contentColor,
        focusedPlaceholderColor = Color.Gray,
        disabledPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray,
        cursorColor = contentColor,
        unfocusedBorderColor = contentColor,
        disabledBorderColor = contentColor,
        focusedBorderColor = contentColor,
        errorBorderColor = Color.Red,
    )
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
    var subject: String, var memo: String, var color: Long
)

data class Notice(
    val text: String? = "메세지 오류",
    val userId: String? = "UID 오류",
    val userName: String? = "이름 오류",
    val timestamp: Any? = null,
    val userImage: String? = "이미지 오류"
)

@Composable
fun CalendarApp(contentColor: Color, backgroundColor: Color) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Calendar header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedDate = selectedDate.minusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Month", tint = contentColor)
            }

            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            IconButton(onClick = { selectedDate = selectedDate.plusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Month", tint = contentColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar grid
        CalendarGridView(selectedDate = selectedDate, contentColor = contentColor)
    }
}

@Composable
fun CalendarGridView(selectedDate: LocalDate, contentColor: Color) {
    val firstDayOfMonth = selectedDate.with(TemporalAdjusters.firstDayOfMonth())
    val lastDayOfMonth = selectedDate.with(TemporalAdjusters.lastDayOfMonth())
    val daysInMonth = (1..lastDayOfMonth.dayOfMonth)
    LazyVerticalGrid(

        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(daysInMonth.toList()) { day ->
            val date = LocalDate.of(selectedDate.year, selectedDate.monthValue, day)
            CalendarDayItem(date = date, contentColor = contentColor)
        }
    }}

@Composable
fun CalendarDayItem(date: LocalDate, contentColor: Color) {
    val dayOfWeek = date.dayOfWeek.value
    val dayOfWeekText = when (dayOfWeek) {
        1 -> "월"
        2 -> "화"
        3 -> "수"
        4 -> "목"
        5 -> "금"
        6 -> "토"
        else -> "일"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        contentAlignment = Alignment.TopCenter, // 요일을 숫자 위에 배치
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfWeekText,
                fontSize = 12.sp,
                color = when (dayOfWeek) {
                    7 -> Color.Red // 일요일
                    6 -> Color.Blue // 토요일
                    else -> contentColor
                },
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (date.monthValue != LocalDate.now().monthValue) Color.Gray else contentColor
            )
        }
    }
}