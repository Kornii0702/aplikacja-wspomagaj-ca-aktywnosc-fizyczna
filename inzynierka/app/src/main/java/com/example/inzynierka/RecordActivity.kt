package com.example.inzynierka

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.BottomNavigationBar
import com.example.inzynierka.ui.theme.AppTheme
import com.example.inzynierka.ui.theme.ThemeManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.UUID



class RecordActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSM configuration
        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )

        ThemeManager.loadTheme(this)

        setContent {
            val darkMode by ThemeManager.isDarkMode.collectAsState()

            AppTheme(darkTheme = darkMode) {
                Scaffold(
                    topBar = { AppTopBar(userName = "User") },
                    bottomBar = { BottomNavigationBar(currentRoute = "Record") }
                ) { padding ->
                    RecordScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun RecordScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showTimer by remember { mutableStateOf(false) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }

    // Request location permission
    LaunchedEffect(Unit) {
        if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
            locationOverlay.enableMyLocation()
            mapView.overlays.add(locationOverlay)
            locationOverlay.runOnFirstFix {
                mapView.controller.setZoom(16.0)
                mapView.controller.setCenter(locationOverlay.myLocation ?: GeoPoint(0.0, 0.0))
            }
        } else {
            ActivityCompat.requestPermissions(
                (context as ComponentActivity),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Show the OSM map
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        // "Start Activity" button
        Button(
            onClick = { showTimer = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
        ) {
            Text("Start Activity")
        }

        // Timer + tracking overlay
        if (showTimer) {
            TimerOverlay(
                mapView = mapView,
                onClose = { showTimer = false }
            )
        }
    }
}

@Composable
fun TimerOverlay(mapView: MapView, onClose: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var timeInSeconds by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    // For tracking user path
    val routeLine = remember { Polyline().apply { color = android.graphics.Color.RED; width = 8f } }
    var trackingJob by remember { mutableStateOf<Job?>(null) }

    val trackedPoints = remember { mutableStateListOf<GeoPoint>() }
    var startTime by remember { mutableStateOf<Date?>(null) }
    var endTime by remember { mutableStateOf<Date?>(null) }

    LaunchedEffect(Unit) {
        mapView.overlays.add(routeLine)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                .padding(24.dp)
        ) {
            Text(
                text = formatTime(timeInSeconds),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // 🟢 Start tracking
                Button(onClick = {
                    if (!isRunning) {
                        isRunning = true
                        startTime = Date()

                        // Start timer
                        scope.launch {
                            while (isRunning) {
                                delay(1000)
                                timeInSeconds++
                            }
                        }

                        // Start tracking location
                        trackingJob = scope.launch {
                            val provider = GpsMyLocationProvider(context)
                            val overlay = MyLocationNewOverlay(provider, mapView)
                            overlay.enableMyLocation()

                            while (isRunning) {
                                val loc = overlay.myLocation
                                if (loc != null) {
                                    val point = GeoPoint(loc.latitude, loc.longitude)
                                    trackedPoints.add(point)
                                    routeLine.addPoint(point)
                                    mapView.invalidate()
                                }
                                delay(2000) // update every 2 seconds
                            }
                        }
                    }
                }) { Text("Start") }

                // 🔴 Stop tracking
                Button(onClick = {
                    isRunning = false
                    endTime = Date()
                    trackingJob?.cancel()
                }) { Text("Stop") }

                // 🔄 Reset
                Button(onClick = {
                    isRunning = false
                    timeInSeconds = 0
                    trackingJob?.cancel()
                    trackedPoints.clear()
                    routeLine.setPoints(emptyList())
                    mapView.invalidate()
                }) { Text("Reset") }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ Save to Firestore
            TextButton(onClick = {
                isRunning = false
                trackingJob?.cancel()
                endTime = Date()

                if (user != null && trackedPoints.isNotEmpty()) {
                    val userId = user.uid
                    val activityId = UUID.randomUUID().toString()

                    // Calculate total distance (simple sum of segment distances)
                    val distanceMeters = calculateTotalDistance(trackedPoints)
                    val avgPace = if (distanceMeters > 0) timeInSeconds / (distanceMeters / 1000) else 0.0

                    val activityData = hashMapOf(
                        "startTime" to startTime,
                        "endTime" to endTime,
                        "durationSeconds" to timeInSeconds,
                        "distanceMeters" to distanceMeters,
                        "averagePace" to avgPace,
                        "route" to trackedPoints.map { mapOf("lat" to it.latitude, "lng" to it.longitude) }
                    )

                    db.collection("users")
                        .document(userId)
                        .collection("activities")
                        .document(activityId)
                        .set(activityData)
                        .addOnSuccessListener {
                            println("✅ Activity saved to Firestore")
                        }
                        .addOnFailureListener {
                            println(" Failed to save activity: ${it.message}")
                        }
                }

                onClose()
            }) {
                Text("End Activity", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val sec = seconds % 60
    return String.format("%02d:%02d", minutes, sec)
}

private fun calculateTotalDistance(points: List<GeoPoint>): Double {
    var total = 0.0
    for (i in 1 until points.size) {
        total += points[i - 1].distanceToAsDouble(points[i])
    }
    return total
}
