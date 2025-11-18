package com.example.inzynierka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.BottomNavigationBar
import com.example.inzynierka.ui.theme.AppTheme
import com.example.inzynierka.ui.theme.ThemeManager
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        ThemeManager.loadTheme(this)

        setContent {
            val darkMode by ThemeManager.isDarkMode.collectAsState()

            AppTheme(darkTheme = darkMode) {
                Scaffold(
                    topBar = { AppTopBar(userName = "User") },
                    bottomBar = { BottomNavigationBar(currentRoute = "Profile") }
                ) { padding ->
                    ProfileScreen(
                        auth = FirebaseAuth.getInstance(),
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(auth: FirebaseAuth, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val user = auth.currentUser
    val darkMode by ThemeManager.isDarkMode.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👤 Profile Screen", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Email: ${user?.email ?: "Unknown"}")
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Dark Mode")
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = darkMode,
                onCheckedChange = { ThemeManager.setDarkMode(ctx, it) }
            )
        }
    }
}
