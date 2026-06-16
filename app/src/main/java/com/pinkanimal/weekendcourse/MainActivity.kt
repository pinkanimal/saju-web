package com.pinkanimal.weekendcourse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pinkanimal.weekendcourse.ui.course.CourseScreen
import com.pinkanimal.weekendcourse.ui.list.PlaceListScreen
import com.pinkanimal.weekendcourse.ui.share.ShareScreen
import com.pinkanimal.weekendcourse.ui.theme.WeekendCourseTheme
import com.pinkanimal.weekendcourse.work.NotificationHelper
import com.pinkanimal.weekendcourse.work.WorkScheduler
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(val route: String, val label: String) {
    object Share : Screen("share", "공유")
    object PlaceList : Screen("place_list", "장소 목록")
    object Course : Screen("course", "코스")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var sharedImageUri = mutableStateOf<Uri?>(null)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored — app works without notification permission */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        handleIntent(intent)
        requestNotificationPermissionIfNeeded()
        if (sharedImageUri.value == null) {
            WorkScheduler.scheduleFridayDigest(this)
        }
        setContent {
            WeekendCourseTheme {
                val navController = rememberNavController()
                val sharedUri by sharedImageUri

                val items = listOf(Screen.Share, Screen.PlaceList, Screen.Course)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        when (screen) {
                                            is Screen.Share -> Icon(Icons.Filled.Share, contentDescription = screen.label)
                                            is Screen.PlaceList -> Icon(Icons.Filled.List, contentDescription = screen.label)
                                            is Screen.Course -> Icon(Icons.Filled.Notifications, contentDescription = screen.label)
                                        }
                                    },
                                    label = { Text(screen.label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (BuildConfig.DEBUG) {
                            FloatingActionButton(
                                onClick = { WorkScheduler.triggerNow(this@MainActivity) }
                            ) {
                                Icon(Icons.Filled.Notifications, contentDescription = "테스트 알림")
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (sharedUri != null) Screen.Share.route else Screen.PlaceList.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Share.route) {
                            if (sharedUri != null) {
                                ShareScreen(imageUri = sharedUri!!)
                            } else {
                                Surface(modifier = Modifier.fillMaxSize()) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Text("스크린샷을 공유해보세요")
                                    }
                                }
                            }
                        }
                        composable(Screen.PlaceList.route) {
                            PlaceListScreen()
                        }
                        composable(Screen.Course.route) {
                            CourseScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND &&
            intent.type?.startsWith("image/") == true
        ) {
            try {
                grantUriPermission(
                    packageName,
                    intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // ignore permission grant errors
            }
            val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
            if (uri != null) {
                sharedImageUri.value = uri
            }
        }
    }
}
