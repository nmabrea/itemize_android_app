package com.example.nabrea.itemizeapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass

@Dao
interface ExpenseDao {

    @Query("SELECT * from expense_table ORDER BY description ASC")
    fun getAlphabetizedExpenses(): LiveData<List<ExpenseDataClass>>

    /*@Query("SELECT * from expense_table ORDER BY expenseID ASC")
    fun getOrderedByIdExpenses(): LiveData<List<ExpenseDataClass>>*/

    @Query("SELECT * from expense_table ORDER BY description ASC")
    fun getOrderedExpenses(): LiveData<List<ExpenseDataClass>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertExpense(expense: ExpenseDataClass)

    @Query("DELETE FROM expense_table")
    suspend fun deleteAllExpenses()

    @Delete
    suspend fun deleteSelectedExpense(expense: ExpenseDataClass)

}