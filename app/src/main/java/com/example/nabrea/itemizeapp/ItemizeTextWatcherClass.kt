package com.example.nabrea.itemizeapp

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber
import java.text.NumberFormat
import java.util.*

class ItemizeTextWatcherClass () {

    fun setCurrencyTextWatcher(editText: TextInputEditText) {
        editText.addTextChangedListener(object: TextWatcher {

            var currentInput = ""

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var userInput = p0.toString()

                if(userInput != currentInput) {
                    editText.removeTextChangedListener(this)

                    val locale: Locale = Locale.US
                    val currency = Currency.getInstance(locale)
                    val cleanString = userInput.replace("[${currency.symbol},.]".toRegex(), "")
                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance(locale).format(parsed / 100)

                    Timber.i("cleanstring: ${cleanString}")

                    userInput = formatted.removePrefix("$")

                    Timber.i("${userInput}")

                    editText.setText(userInput)
                    editText.setSelection(userInput.length)
                    editText.addTextChangedListener(this)
                }
            }

        })
    }

}