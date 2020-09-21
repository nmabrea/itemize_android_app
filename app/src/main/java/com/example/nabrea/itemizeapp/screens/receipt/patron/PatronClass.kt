package com.example.nabrea.itemizeapp.screens.receipt.patron

import androidx.lifecycle.LiveData
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseClass

class PatronClass(val firstName: LiveData<String>,
                  val lastName: LiveData<String>,
                  val patronID: LiveData<Int>,
                  val cart: LiveData<MutableList<ExpenseClass>> ) {
}