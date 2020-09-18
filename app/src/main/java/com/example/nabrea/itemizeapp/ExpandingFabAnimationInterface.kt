package com.example.nabrea.itemizeapp

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

interface ExpandingFabAnimationInterface {

    // Initialize as Fragment Context or Activity Context
    val animationContext: Context

    // Animation for showcasing an open menu
    val speedDialOpen: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.anim_open)

    // Animation for showcasing a closed menu
    val speedDialClose: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.anim_close)

    // Animation for Main FAB when menu is opening
    val rotateAntiClockwise: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.anim_rotate_anti_clockwise_end)

    // Animation for Main FAB when menu is closing
    val rotateClockwise: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.anim_rotate_clockwise_end)

    val fadeIn: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.nav_default_enter_anim)

    val fadeOut: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.nav_default_exit_anim)

    val slideInToHalfway: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.anim_slide_in_up_half)

    val slideOutFromHalfway: Animation
        get() = AnimationUtils.loadAnimation(animationContext, R.anim.anim_slide_out_down_half)

    fun animateFabOpening(actionsAvailable: MutableMap<Pair<FloatingActionButton, MaterialTextView>, *>) {
        actionsAvailable.forEach {

            it.key.first.startAnimation(speedDialOpen)
            it.key.first.show()
            it.key.first.isClickable = true
            it.key.first.isLongClickable = true

            it.key.second.startAnimation(speedDialOpen)
            it.key.second.visibility = MaterialTextView.VISIBLE
        }
    }

    fun animateFabClosing(actionsAvailable: MutableMap<Pair<FloatingActionButton, MaterialTextView>, *>) {
        actionsAvailable.forEach {

            it.key.first.startAnimation(speedDialClose)
            it.key.first.visibility = FloatingActionButton.GONE
            it.key.first.isClickable = false
            it.key.first.isLongClickable = false

            it.key.second.startAnimation(speedDialClose)
            it.key.second.visibility = MaterialTextView.GONE
        }
    }


    fun animateFabAntiClockwise(view: FloatingActionButton) { view.startAnimation(rotateAntiClockwise) }

    fun animateFabClockwise(view: FloatingActionButton) { view.startAnimation(rotateClockwise) }
}