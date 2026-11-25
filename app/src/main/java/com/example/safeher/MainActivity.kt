package com.example.safeher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.safeher.navigation.SafeHerNavGraph
import com.example.safeher.ui.theme.SafeHerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeHerTheme {
                val navController = rememberNavController()
                SafeHerNavGraph(navController = navController)
            }
        }
    }
}
