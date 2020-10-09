package com.example.nabrea.itemizeapp.screens.receipt.patron

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patron_table")
data class PatronDataClass(

    @PrimaryKey(autoGenerate = true)
    var patronID: Long? = 0L,

    @ColumnInfo(name = "name")
    var name: String = "Placeholder",

    @ColumnInfo(name = "initials")
    var nameInitials: String = "PH",

    /*@ColumnInfo(name = "cart")
    val cart: MutableList<ExpenseDataClass> = mutableListOf()*/
)