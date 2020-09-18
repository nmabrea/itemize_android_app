package com.example.nabrea.itemizeapp

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber



class PrimaryButtonClass(val button: FloatingActionButton,
                         private val navController: NavController?,
                         private val mainBab: BottomAppBar,
                         private val primaryDirection: NavDirections,
                         override val animationContext: Context
) : ExpandingFabAnimationInterface {

    var isClosed: Boolean = true

    // Setting button behavior depending on its position along the bottom app bar
    fun setMainClickBehavior() {
        when (mainBab.fabAlignmentMode) {

            // Center position is used for when it's the only action
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER -> {
                Timber.i("PrimaryButton will Navigate")

                button.isClickable
                button.setOnClickListener {
                    navController?.navigate(primaryDirection)
                }
            }
            // End position is when it initiates an expanding menu
            BottomAppBar.FAB_ALIGNMENT_MODE_END -> {
                Timber.i("PrimaryButton is now a Menu")

                button.isClickable
            }
        }
    }
}