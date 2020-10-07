package com.example.nabrea.itemizeapp.activity

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination


interface NavigationHostInterface {

    val navController: NavController

    var drawerLayout: DrawerLayout

    // Only allows the Navigation Drawer to be opened from the starting Fragment
    fun drawerMode() {
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, _ ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }
}