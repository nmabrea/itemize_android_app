package com.example.nabrea.itemizeapp.database

import androidx.lifecycle.LiveData
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass
import timber.log.Timber

class ReceiptRepository(
    private val expenseDao: ExpenseDao,
    private val patronDao: PatronDao
) {

    val allExpenses: LiveData<List<ExpenseDataClass>> = expenseDao.getOrderedExpenses()

    val allPatrons: LiveData<List<PatronDataClass>> = patronDao.getAlphabetizedPatrons()

    suspend fun insertExpense(expense: ExpenseDataClass) {

        expenseDao.insertExpense(expense)

    }

    suspend fun updateExpense(expense: ExpenseDataClass) {

        expenseDao.updateExpense(expense)

    }

    suspend fun deleteExpense(expense: ExpenseDataClass) {
        Timber.i("deleteExpense() is called")
        expenseDao.deleteSelectedExpense(expense)
    }

    fun insertPatron(patron: PatronDataClass) {
        patronDao.insertPatron(patron)
    }
}