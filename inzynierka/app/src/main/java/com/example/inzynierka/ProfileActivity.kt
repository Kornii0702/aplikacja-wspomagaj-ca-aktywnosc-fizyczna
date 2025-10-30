package com.example.inzynierka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            Scaffold(
                topBar = { AppTopBar(userName = auth.currentUser?.displayName ?: "User") },
                bottomBar = { BottomNavigationBar(currentRoute = "Profile") }
            ) { padding ->
                ProfileScreen(auth, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun ProfileScreen(auth: FirebaseAuth, modifier: Modifier = Modifier) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val user = auth.currentUser

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👤 Profile Screen", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(8.dp))
        Text("Email: ${user?.email}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            auth.signOut()
            Toast.makeText(ctx, "Signed out", Toast.LENGTH_SHORT).show()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java))
        }) {
            Text("Sign out")
        }
    }
}
