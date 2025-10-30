package com.example.inzynierka.ui.components

import android.content.Intent
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.inzynierka.HomeActivity
import com.example.inzynierka.RecordActivity
import com.example.inzynierka.ProfileActivity

sealed class NavItem(val title: String, val icon: ImageVector) {
    object Home : NavItem("Home", Icons.Default.Home)
    object Record : NavItem("Record", Icons.Default.List)
    object Profile : NavItem("Profile", Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(currentRoute: String) {
    val ctx = LocalContext.current

    BottomNavigation {
        val items = listOf(NavItem.Home, NavItem.Record, NavItem.Profile)

        items.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.title,
                onClick = {
                    when (item) {
                        is NavItem.Home -> {
                            if (currentRoute != "Home")
                                ctx.startActivity(Intent(ctx, HomeActivity::class.java))
                        }
                        is NavItem.Record -> {
                            if (currentRoute != "Record")
                                ctx.startActivity(Intent(ctx, RecordActivity::class.java))
                        }
                        is NavItem.Profile -> {
                            if (currentRoute != "Profile")
                                ctx.startActivity(Intent(ctx, ProfileActivity::class.java))
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}
