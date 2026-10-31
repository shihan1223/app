package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alarm.NotificationHelper
import com.example.ui.navigation.MediMealNavGraph
import com.example.ui.theme.MediMealTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create Notification channels immediately on app start
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannels()

        setContent {
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = userProfile?.isDarkMode ?: systemDark

            MediMealTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MediMealNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
