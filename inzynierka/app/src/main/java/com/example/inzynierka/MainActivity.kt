package com.example.inzynierka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.NavItem
import com.example.inzynierka.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        // ✅ Check if user is already logged in
        val user = auth.currentUser
        if (user == null) {
            // If not logged in, go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // prevent going back here before login
            return
        }
        ThemeManager.loadTheme(this)

        // Otherwise, show the main screen
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

@Composable
fun WelcomeScreen(auth: FirebaseAuth) {
    val ctx = LocalContext.current
    val user = auth.currentUser
    val displayName =  user?.email?.substringBefore('@') ?: "User"


    Scaffold(
        topBar = { AppTopBar(userName = displayName) } // 👈 Reusable component
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to FitApp", style = MaterialTheme.typography.h4)
            Spacer(Modifier.height(16.dp))
            if (user != null) {
                Text(text = "Signed in as: ${user.email}")
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    auth.signOut()
                    Toast.makeText(ctx, "Signed out", Toast.LENGTH_SHORT).show()
                    ctx.startActivity(Intent(ctx, LoginActivity::class.java))
                    (ctx as? ComponentActivity)?.finish()
                }) { Text("Sign out") }
            }
        }
    }
}
