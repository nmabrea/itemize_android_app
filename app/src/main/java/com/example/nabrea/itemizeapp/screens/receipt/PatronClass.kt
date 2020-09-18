package com.example.nabrea.itemizeapp.screens.receipt

import androidx.lifecycle.LiveData

class PatronClass(val firstName: LiveData<String>,
                  val lastName: LiveData<String>,
                  val patronID: LiveData<Int>,
                  val cart: LiveData<MutableList<ExpenseClass>> ) {
}