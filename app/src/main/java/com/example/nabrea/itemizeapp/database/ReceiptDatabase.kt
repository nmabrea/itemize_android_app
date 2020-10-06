package com.example.nabrea.itemizeapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(
    entities = [
        ExpenseDataClass::class,
        PatronDataClass::class
    ],
    version = 6,
    exportSchema = false
)

abstract class ReceiptDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun patronDao(): PatronDao

    private class ReceiptDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.expenseDao())
                }
            }
        }

        suspend fun populateDatabase(expenseDao: ExpenseDao) {
            expenseDao.deleteAllExpenses()

            val expense =
                ExpenseDataClass(
                    0,
                    "Example Expense",
                    15F,
                    "$15.00",
                    2,
                    30F,
                    "$30.00",
                    0 )

            expenseDao.insertExpense(expense)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ReceiptDatabase? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): ReceiptDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReceiptDatabase::class.java,
                    "receipt_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}