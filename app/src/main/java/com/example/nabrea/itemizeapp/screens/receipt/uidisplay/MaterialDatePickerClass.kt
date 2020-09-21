package com.example.nabrea.itemizeapp.screens.receipt.uidisplay

import androidx.fragment.app.FragmentManager
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragmentCommunication
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText

class MaterialDatePickerClass(
    private val editText: TextInputEditText,
    private val fragManager: FragmentManager) {

    private val builder = MaterialDatePicker.Builder.datePicker()

    private val picker = builder.build()

    fun setTitle() { builder.setTitleText("SELECT A DATE") }

    fun clickActions(listener: ReceiptFragmentCommunication) {
        editText.setOnClickListener { editTextView ->

            picker.show(fragManager, picker.toString())

        }

        picker.addOnPositiveButtonClickListener { dateSelected ->

            editText.setText("${picker.headerText}")

            listener.displaySnackbar("Date Selected: ${editText.text}.")

        }

        picker.addOnCancelListener { cancelled ->
            listener.displaySnackbar("Calendar closed.")
        }

        picker.addOnNegativeButtonClickListener { cancelled ->
            listener.displaySnackbar("Calendar closed.")
        }

    }
}