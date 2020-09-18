package com.example.nabrea.itemizeapp.screens.receipt

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FormValidator(val formFields: MutableMap<TextInputLayout, TextInputEditText> = mutableMapOf(),
                    val errorMessage: String) {

    fun isValidInput() : Boolean{
        formFields.forEach { pair ->
            if (pair.value.text == null) {
                pair.key.error = errorMessage
                return false
            } else {
                pair.key.error = null
            }
        }
        return true
    }

}