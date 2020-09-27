package com.example.nabrea.itemizeapp.database

import androidx.lifecycle.LiveData
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass

class ReceiptRepository(
    private val expenseDao: ExpenseDao,
    private val patronDao: PatronDao
) {

    val allExpenses: LiveData<List<ExpenseDataClass>> = expenseDao.getAlphabetizedExpenses()

    val allPatrons: LiveData<List<PatronDataClass>> = patronDao.getAlphabetizedPatrons()

    fun insertExpense(expense: ExpenseDataClass) {
        expenseDao.insertExpense(expense)
    }

    fun insertPatron(patron: PatronDataClass) {
        patronDao.insertPatron(patron)
    }
}