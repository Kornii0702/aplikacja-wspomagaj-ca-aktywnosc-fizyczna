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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        // ✅ Call setContent and show the composable
        setContent {
            RegisterScreen(auth = auth) {
                finish() // back to LoginActivity after success
            }
        }
    }
}

@Composable
fun RegisterScreen(auth: FirebaseAuth, onSuccess: () -> Unit) {
    val ctx = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", style = MaterialTheme.typography.h5)
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

        Button(
            onClick = {
                loading = true
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(ctx, "Enter email and password", Toast.LENGTH_SHORT).show()
                    loading = false
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        loading = false
                        if (task.isSuccessful) {
                            Toast.makeText(ctx, "Account created!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(
                                ctx,
                                "Registration failed: ${task.exception?.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading)
                CircularProgressIndicator(modifier = Modifier.height(20.dp))
            else
                Text("Register")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { (ctx as? ComponentActivity)?.finish() }) {
            Text("Back")
        }
    }
}