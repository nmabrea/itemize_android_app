package com.example.nabrea.itemizeapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass

@Dao
interface PatronDao {

    @Query("SELECT * from patron_table ORDER BY patronID ASC")
    fun getOrderedPatrons(): LiveData<List<PatronDataClass>>

    @Query("SELECT * from patron_table ORDER BY name ASC")
    fun getAlphabetizedPatrons(): LiveData<List<PatronDataClass>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPatron(patron: PatronDataClass)

    @Update(entity = PatronDataClass::class)
    suspend fun updatePatron(patron: PatronDataClass)

    @Query("DELETE FROM patron_table")
    suspend fun deleteAllPatrons()

    @Delete
    suspend fun deleteSelectedPatron(patron: PatronDataClass)
}