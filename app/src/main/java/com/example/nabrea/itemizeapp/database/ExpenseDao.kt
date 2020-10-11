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

    @Update(entity = ExpenseDataClass::class)
    suspend fun updateExpense(expense: ExpenseDataClass)

    @Query("SELECT SUM(sub_cost_raw) FROM expense_table")
    fun getTotalExpenseCost(): LiveData<Float>

    @Query("DELETE from expense_table")
    suspend fun deleteAllExpenses()

    @Delete
    suspend fun deleteSelectedExpense(expense: ExpenseDataClass)

}