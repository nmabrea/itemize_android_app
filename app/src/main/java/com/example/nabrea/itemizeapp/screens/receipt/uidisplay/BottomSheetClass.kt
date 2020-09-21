package com.example.nabrea.itemizeapp.screens.receipt.uidisplay

import android.content.Context
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nabrea.itemizeapp.ExpandingFabAnimationInterface
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragment
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragmentCommunication
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber

class BottomSheetClass(
    bottomSheetLayout: ConstraintLayout,
    val background: LinearLayoutCompat,
    val listener: ReceiptFragmentCommunication,
    val host: ReceiptFragment,
    override val animationContext: Context
) : BottomSheetBehavior<ConstraintLayout>(),
    ExpandingFabAnimationInterface {

    // Identifying the layout to attach BottomSheetBehavior to.
    var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> = from(bottomSheetLayout)

    init {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback()
        {

            // Override function required by the BottomSheetCallback
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {

                    // Programmed reactions for UI-related views during STATE_COLLAPSED
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                        // Listener is notified that the BottomSheetState is STATE_COLLAPSED
                        listener.onBottomSheetCollapsed()

                        // Background will fade out
                        background.startAnimation(fadeOut)

                        // Background is gone from the screen
                        background.visibility = LinearLayoutCompat.GONE

                        host.setBottomSheetUnfocused()

                        Timber.i("BottomSheet is Collapsed")
                    }

                    // Programmed reactions for UI-related views during STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        // Listener is notified that the BottomSheetState is STATE_EXPANDED
                        listener.onBottomSheetExpanded()

                        // Background is fading in
                        background.startAnimation(fadeIn)

                        // Background is visible within the screen
                        background.visibility = LinearLayoutCompat.VISIBLE

                        host.setBottomSheetFocus()

                        Timber.i("BottomSheet is fully Expanded")
                    }

                    // Programmed reactions for UI-related views during STATE_DRAGGING
                    BottomSheetBehavior.STATE_DRAGGING ->
                        Timber.i("BottomSheet is Dragging")

                    // Programmed reactions for UI-related views during STATE_SETTLING
                    BottomSheetBehavior.STATE_SETTLING ->
                        Timber.i("BottomSheet is Settling")

                    // Programmed reactions for UI-related views during STATE_HIDDEN
                    BottomSheetBehavior.STATE_HIDDEN ->
                        Timber.i("BottomSheet is Hidden")

                    // Programmed reactions for UI-related views during other states
                    else -> return
                }
            }
            // Programmed reactions for UI-related views while the BottomSheet is sliding
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Timber.i("onSlide is called")
            }
        })
    }

}