package com.example.nabrea.itemizeapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass

@Dao
interface ExpenseDao {

    @Query("SELECT * from expense_table ORDER BY description ASC")
    fun getAlphabetizedExpenses() : LiveData<List<ExpenseDataClass>>

    @Query("SELECT * from expense_table ORDER BY expenseId ASC")
    fun getOrderedExpenses(): LiveData<List<ExpenseDataClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseDataClass) : Long

    @Query("DELETE from expense_table")
    suspend fun deleteAllExpenses()

    @Delete
    suspend fun deleteSelectedExpense(expense: ExpenseDataClass)

}