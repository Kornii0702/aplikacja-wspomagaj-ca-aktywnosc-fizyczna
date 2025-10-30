package com.example.inzynierka

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.example.inzynierka.ui.components.AppTopBar
import com.example.inzynierka.ui.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class RecordActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Initialize OSM configuration
        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContent {
            Scaffold(
                topBar = { AppTopBar(userName = auth.currentUser?.displayName ?: "User") },
                bottomBar = { BottomNavigationBar(currentRoute = "Record") }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    RecordMapScreen()
                }
            }
        }
    }
}

@Composable
fun RecordMapScreen() {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Request location permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                mapView?.let {
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), it)
                    locationOverlay.enableMyLocation()
                    it.overlays.add(locationOverlay)
                    it.controller.animateTo(locationOverlay.myLocation)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    AndroidView(
        factory = {
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(52.2297, 21.0122)) // Default: Warsaw
                mapView = this
            }
        },
        update = { view ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), view)
                locationOverlay.enableMyLocation()
                view.overlays.add(locationOverlay)
                view.controller.setZoom(16.0)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}