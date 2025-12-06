package com.pillup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.pillup.presentation.navigation.NavManager
import com.pillup.ui.theme.PillUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillUpTheme {
                val navController = rememberNavController()
                NavManager(navController)
            }
        }
    }
}
