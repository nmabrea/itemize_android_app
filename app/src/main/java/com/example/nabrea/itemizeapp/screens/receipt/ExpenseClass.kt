package com.example.nabrea.itemizeapp.screens.receipt

import androidx.lifecycle.LiveData

class ExpenseClass(val description: LiveData<String>,
                   var cost: LiveData<Float>,
                   var costFormat: LiveData<String>,
                   var quantity: LiveData<Int>,
                   var subCost: LiveData<Float>,
                   var subCostFormat: LiveData<String>,
                   var essentialRating: LiveData<Int>
) {

    // TODO (10) Format subcost and cost
}