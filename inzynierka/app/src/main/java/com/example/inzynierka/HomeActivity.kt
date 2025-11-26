//package com.example.inzynierka
//
//import android.R
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Switch
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.example.inzynierka.ui.components.AppTopBar
//import com.example.inzynierka.ui.components.BottomNavigationBar
//import com.example.inzynierka.ui.theme.AppTheme
//import com.example.inzynierka.ui.theme.ThemeManager
//import com.google.firebase.auth.FirebaseAuth
//import androidx.compose.material3.*
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//class HomeActivity : ComponentActivity() {
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()
//        ThemeManager.loadTheme(this)
//
//        setContent {
//            val darkMode by ThemeManager.isDarkMode.collectAsState()
//
//            AppTheme(darkTheme = darkMode) {
//                Scaffold(
//                    topBar = {
//                        AppTopBar(
//                            userName = auth.currentUser?.displayName
//                                ?: auth.currentUser?.email?.substringBefore('@')
//                                ?: "User"
//                        ) },
//                    contentWindowInsets = WindowInsets(0,0,0,0),
//                    bottomBar = {
//                        BottomNavigationBar(currentRoute = "Home")
//                    }
//                ) { padding ->
//                    HomeScreen(
//                        auth = FirebaseAuth.getInstance(),
//                        db = db,
//                        modifier = Modifier.padding(padding)
//                    )
//                }
//            }
//        }
//
//
//
//
//
//    }
//}
//
//@Composable
//fun HomeScreen(auth: FirebaseAuth, db: FirebaseFirestore, modifier: Modifier = Modifier) {
//    val darkMode by ThemeManager.isDarkMode.collectAsState()
//    val ctx = LocalContext.current
//    val user = auth.currentUser
//
//    var noteText by remember { mutableStateOf("") }
//    var notesList by remember { mutableStateOf(listOf<String>()) }
//
//    DisposableEffect(Unit) {
//        val listener = db.collection("users")
//            .document(user?.uid ?: "noUserId")
//            .collection("notes")
//            .orderBy("timestamp")
//            .addSnapshotListener { snapshot,  _ ->
//                if(snapshot != null) {
//                    notesList = snapshot.documents.mapNotNull { it.getString("text") }
//                }
//            }
//        onDispose { listener.remove() }
//    }
//    Column(
//        modifier = modifier
//            .fillMaxSize()
////            .padding(horizontal = 16.dp, vertical = 8.dp),
//            .padding(24.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(" Home Screen")
//    }
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Text("Dark Mode")
//        Spacer(Modifier.width(12.dp))
//        Switch(
//            checked = darkMode,
//            onCheckedChange = { ThemeManager.setDarkMode(ctx, it) }
//        )
//    }
////
////
////    Row(
////        modifier = Modifier.fillMaxWidth(),
////        horizontalArrangement = Arrangement.spacedBy(8.dp),
////        verticalAlignment = Alignment.CenterVertically
////    ) {
////        OutlinedTextField(
////            value = noteText,
////            onValueChange = {noteText = it},
////            label = {Text("Write a note")},
////            modifier = Modifier.weight(1f)
////        )
////        Button(
////            onClick = {
////                if(noteText.isNotBlank()) {
////                    addNoteForUser(db, user?.uid ?: "noUserId",noteText) {
////                        notesList = notesList + noteText
////                        noteText = ""
////                    }
////                }
////            }
////        ) { Text("Add")}
////    }
////
////
////    LazyColumn(
////        modifier = Modifier
////            .fillMaxWidth(),
////        verticalArrangement = Arrangement.spacedBy(8.dp)
////    ) {
////        items(notesList) { note ->
////            Card(
////                modifier = Modifier.fillMaxWidth(),
////                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
////            ) {
////                Text(
////                    text = note,
////                    modifier = Modifier.padding(16.dp),
////                    style = MaterialTheme.typography.bodyLarge
////                )
////            }
////        }
////    }
//}
//
//private fun addNoteForUser(db: FirebaseFirestore, userId: String, text: String, onComplete: () -> Unit) {
//    val noteData = hashMapOf("text" to text, "timestamp" to System.currentTimeMillis())
//    db.collection("users")
//        .document(userId)
//        .collection("notes")
//        .add(noteData)
//        .addOnSuccessListener { onComplete() }
//}
//
//private suspend fun loadNotesForUser(db: FirebaseFirestore, userId: String): List<String> {
//    return try {
//        val snapshot = db.collection("users")
//            .document(userId)
//            .collection("notes")
//            .orderBy("timestamp")
//            .get()
//            .await()
//
//        snapshot.documents.mapNotNull { it.getString("text") }
//    } catch (e: Exception) {
//        emptyList()
//    }
//}

package com.example.inzynierka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.inzynierka.ui.theme.NoteItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        ThemeManager.loadTheme(this)

        setContent {
            val darkMode by ThemeManager.isDarkMode.collectAsState()

            AppTheme(darkTheme = darkMode) {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            userName = auth.currentUser?.displayName
                                ?: auth.currentUser?.email?.substringBefore('@')
                                ?: "User"
                        ) },
                    bottomBar = {
                        BottomNavigationBar(currentRoute = "Home")
                    }
                ) { padding ->
                    HomeScreen(
                        auth = FirebaseAuth.getInstance(),
                        db = FirebaseFirestore.getInstance(),
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }





    }
}

@Composable
fun HomeScreen(auth: FirebaseAuth, db: FirebaseFirestore, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val darkMode by ThemeManager.isDarkMode.collectAsState()
    val user = auth.currentUser ?: return

    var noteText by remember { mutableStateOf("") }
    var notesList by remember { mutableStateOf(listOf<NoteItem>()) }

    // Load notes when screen opens
    DisposableEffect(Unit) {
        val listener = db.collection("users")
            .document(user.uid)
            .collection("notes")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                   notesList = snapshot.documents.mapNotNull { doc ->
                   val text = doc.getString("text")
                   if (text != null) NoteItem(id = doc.id, text = text) else null
                   }
                }
            }
        onDispose { listener.remove() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp), // smaller padding
        verticalArrangement = Arrangement.Top, // 👈 key change — start at top, not center
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "🏠 Home Screen",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = notesList.size.toString()
        )

        Spacer(Modifier.height(16.dp))

        // Dark Mode switch
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

        Spacer(Modifier.height(24.dp))

        // Note input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Write a note...") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        addNoteForUser(db, user.uid, noteText) { newNote ->
                            noteText = ""

                        }
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Make list scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // 👈 lets list expand and scroll inside available space
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notesList) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = note.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                deleteNoteForUser(db, user.uid, note.id) {
                                    notesList = notesList.filterNot { it.id == note.id }
                                }
                            }
                        ) {
                            Text("delete")
                        }
                    }
                }
            }
        }
    }
}
    private fun deleteNoteForUser(
    db: FirebaseFirestore,
    userId: String,
    noteId: String,
    onComplete: () -> Unit
) {
    db.collection("users")
        .document(userId)
        .collection("notes")
        .document(noteId)
        .delete()
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener { exception ->  exception.printStackTrace() }
}


private fun addNoteForUser(
    db: FirebaseFirestore,
    userId: String,
    text: String,
    onComplete: (NoteItem) -> Unit // ✅ now lambda accepts a NoteItem
) {
    val noteData = hashMapOf(
        "text" to text,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("users")
        .document(userId)
        .collection("notes")
        .add(noteData)
        .addOnSuccessListener { doc ->
            onComplete(NoteItem(id = doc.id, text = text))
        }
}


