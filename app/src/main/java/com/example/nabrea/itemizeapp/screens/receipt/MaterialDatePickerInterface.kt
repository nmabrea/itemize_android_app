package com.example.nabrea.itemizeapp.screens.receipt

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText

interface MaterialDatePickerInterface {

    var builder : MaterialDatePicker.Builder<*>

    fun setTitle() { builder.setTitleText("SELECT A DATE") }

    var picker : MaterialDatePicker<*>

    var editDateText: TextInputEditText

    var fragManager: FragmentManager


    fun clickActions(listener: ReceiptFragmentCommunication) {
        editDateText.setOnClickListener { editTextView ->

            picker.show(fragManager, picker.toString())

        }

        picker.addOnPositiveButtonClickListener { dateSelected ->

            editDateText.setText("${picker.headerText}")

            listener.displaySnackbar("Date Selected: ${editDateText.text}")

        }

    }
}