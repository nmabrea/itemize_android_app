package com.example.nabrea.itemizeapp.screens.receipt.expense

class ExpenseBasketClass( val expenseList: MutableList<ExpenseClass>) {

    var expenseTotal: Float? = null


    fun findTotal() {

        val totalRough = expenseList.sumByDouble { expense -> (expense.subCost.value)!!.toDouble() }

        val totalFine = "%.2f".format(totalRough)

    }
}