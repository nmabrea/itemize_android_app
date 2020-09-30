package com.example.nabrea.itemizeapp.screens.receipt.expense

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class ExpenseDataClass(

    @PrimaryKey
    @NonNull
    val description: String = "placeholder",

    @ColumnInfo(name = "cost_raw")
    var cost: Float = 0F,

    @ColumnInfo(name = "cost_formatted")
    var costFormat: String = "placeholder",

    @ColumnInfo(name = "quantity")
    var quantity: Int = 0,

    @ColumnInfo(name = "sub_cost_raw")
    var subCost: Float = 0F,

    @ColumnInfo(name = "sub_cost_formatted")
    var subCostFormat: String = "$0.00",

    @ColumnInfo(name = "essential_rating")
    var essentialRating: Int?
)