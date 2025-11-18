package com.example.inzynierka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.BottomNavigationBar
import com.example.inzynierka.ui.theme.AppTheme
import com.example.inzynierka.ui.theme.ThemeManager
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        ThemeManager.loadTheme(this)

        setContent {
            // Load current dark/light mode from ThemeManager
            val darkMode by ThemeManager.isDarkMode.collectAsState()

            // Apply your Material 3 theme reactively
            AppTheme(darkTheme = darkMode) {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            userName = auth.currentUser?.displayName
                                ?: auth.currentUser?.email?.substringBefore('@')
                                ?: "User"
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(currentRoute = "Home")
                    }
                ) { padding ->
                    HomeScreen(Modifier.padding(padding))
                }
            }
        }





    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏠 Home Screen", style = MaterialTheme.typography.h5)
    }
}
