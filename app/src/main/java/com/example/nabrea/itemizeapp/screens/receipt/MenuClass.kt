package com.example.nabrea.itemizeapp.screens.receipt

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.nabrea.itemizeapp.ExpandingFabAnimationInterface
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber


class MenuClass(var navController: NavController,
                override var animationContext: Context
) : ExpandingFabAnimationInterface {

    var menuSheetActions: MutableMap<
            Pair<FloatingActionButton, MaterialTextView>, BottomSheetClass> = mutableMapOf()

    var menuNavActions: MutableMap<
            Pair<FloatingActionButton, MaterialTextView>, NavDirections> = mutableMapOf()

    fun setOpeningMenuAnimations() {
        Timber.i("setOpeningMenuAnimations() is called")

        // Setting the animation behavior for the menu actions with BottomSheetBehavior
        animateFabOpening(menuSheetActions)

        // Setting the animation behavior for the menu actions with NavDirections
        animateFabOpening(menuNavActions)

    }

    fun setClosingMenuAnimations() {
        Timber.i("setClosingMenuAnimations() is called")

        // Setting the animation behavior for the menu actions with BottomSheetBehavior
        animateFabClosing(menuSheetActions)

        // Setting the animation behavior for the menu actions with NavDirections
        animateFabClosing(menuNavActions)

    }




    fun setBottomSheetListener() {
        Timber.i("setBottomSheetListener() is called")

        menuSheetActions.forEach { entry ->

            entry.key.first.setOnClickListener { button ->

                // Behavior for what to do during STATE EXPANDED or STATE_COLLAPSED
                when (entry.value.bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // onClick, BottomSheetBehavior is from STATE_COLLAPSED to STATE_EXPANDED
                        entry.value.bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                        // MENU UI::FABs associated with NavDirections are set to close
                        animateFabClosing(menuNavActions)

                        // MENU UI::FABs associated with BottomSheetBehavior are set to close
                        animateFabClosing(menuSheetActions)

                    }
                    else -> {

                        // MENU UI::Behavior setup for Navigation components during STATE_EXPANDED
                        entry.value.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                    }
                }
            }
        }
    }


    fun setNavigationActionListener() {
        Timber.i("setNavigationActionListener() is called")

        // Setting the behavior of the menu after an action item with NavDirections is chosen.
        menuNavActions.forEach {entry ->

            // Locating a direction for the NavController
            val direction = entry.value

            // Closing the menu after an action button has been pressed.
            entry.key.first.setOnClickListener { button ->

                // Closing animations for FABs associated with NavDirections
                animateFabClosing(menuNavActions)

                // Closing animations for FABs associated with BottomSheetBehavior
                animateFabClosing(menuSheetActions)

                // Navigation based on clicked action's associated NavDirections
                navController.navigate(direction)

            }
        }
    }
}
