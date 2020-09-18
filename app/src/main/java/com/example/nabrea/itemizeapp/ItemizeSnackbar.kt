package com.example.nabrea.itemizeapp

import android.view.View
import com.google.android.material.snackbar.Snackbar

class ItemizeSnackbar {

    fun displaySnackbarAction (context: View, message: String, anchorView: View) {
        Snackbar
            .make(context, message, Snackbar.LENGTH_INDEFINITE)
            .setAnchorView(anchorView)
            .setAction("Dismiss") { onTextClick ->
                Snackbar.Callback.DISMISS_EVENT_ACTION
            }
            .show()
    }

    fun displaySnackbarNoAction (context: View, message: String, anchorView: View) {
        Snackbar
            .make(context, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(anchorView)
            .show()
    }
}