package com.example.inzynierka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.BottomNavigationBar
import com.example.inzynierka.ui.theme.AppTheme

import com.example.inzynierka.ui.theme.ThemeManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        ThemeManager.loadTheme(this)

//        setContent {
//            val darkMode by ThemeManager.isDarkMode.collectAsState()
//
//            AppTheme(darkTheme = darkMode) {
//                LoginScreen(
//                    auth = auth,
//                    onSuccess = {
//                        finish() // Close LoginActivity after successful login
//                    }
//                )
//            }
//        }
        setContent {
            val darkMode by ThemeManager.isDarkMode.collectAsState()

            AppTheme(darkTheme = darkMode) {
                    LoginScreen(
                        auth = auth,
                        onSuccess = {
                            finish() // Close LoginActivity after successful login
                        }
                    )
                }
            }
        }


    }



@Composable
fun LoginScreen(auth: FirebaseAuth, onSuccess: () -> Unit) {
    val ctx = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Log in", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // SignIn Button
        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(ctx, "Enter email and password", Toast.LENGTH_SHORT).show()
                    loading = false
                    return@Button
                }

                auth.signInWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        loading = false
                        if (task.isSuccessful) {
                            loading = true
                            ctx.startActivity(Intent(ctx, MainActivity::class.java))
                            Toast.makeText(ctx, "Signed in", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(
                                ctx,
                                "Sign in failed: ${task.exception?.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading)
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else
                Text("Sign in")
        }

        Spacer(Modifier.height(8.dp))


        Spacer(Modifier.height(8.dp))
        // 🔹 Register Button
        Button(
            onClick = {
                ctx.startActivity(Intent(ctx, RegisterActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
        TextButton(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(ctx, "Enter your email to reset password", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    auth.sendPasswordResetEmail(email.trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    ctx,
                                    "Password reset email sent. Check your inbox.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    ctx,
                                    "Error: ${task.exception?.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        ) {
            Text("Forgot Password?")
        }
        Spacer(Modifier.height(8.dp))

    }
}
