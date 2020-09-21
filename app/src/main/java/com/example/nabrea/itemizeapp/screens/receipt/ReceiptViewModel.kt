package com.example.nabrea.itemizeapp.screens.receipt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nabrea.itemizeapp.EventClass
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseBasketClass
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronClass
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.BottomSheetClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber

class ReceiptViewModel : ViewModel() {
    private var _message = MutableLiveData<EventClass<String>>()
    val message: LiveData<EventClass<String>>
        get() = _message

    private var _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    /*private val _errorDescription = MutableLiveData<String>()
    val errorDescription: LiveData<String>
        get() = _errorDescription

    private val _errorCost = MutableLiveData<String>()
    val errorCost: LiveData<String>
        get() = _errorQuantity

    private val _errorQuantity = MutableLiveData<String>()
    val errorQuantity: LiveData<String>
        get() = _errorQuantity*/

    /* Below is the code logic for creating Expense objects through the ExpenseClass
     * Variables are affected by userinput within the bottom_sheet_expense.xml
     */
    val _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    var _costText = MutableLiveData<String>()

    private val _costFormat = MutableLiveData<String>()
    val costFormat: LiveData<String>
        get() = _costFormat

    private val _cost = MutableLiveData<Float>()
    val cost: LiveData<Float>
        get() = _cost

    val _quantityText = MutableLiveData<String>()

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int>
        get() = _quantity

    private val _subCost = MutableLiveData<Float>()
    val subCost: LiveData<Float>
        get() = _subCost

    private val _subCostFormat =  MutableLiveData<String>()
    val subCostFormat: LiveData<String>
        get() = _subCostFormat

    private val _essentialRating = MutableLiveData<Int>()
    val essentialRating: LiveData<Int>
        get() = _essentialRating

    lateinit var expense: ExpenseClass

    val expenseList: MutableList<ExpenseClass> = mutableListOf()

    // Below is the code logic for compiling UserInput for Expenses and Patrons
    val expenseBasket = ExpenseBasketClass(expenseList)

    private val _expenseRecord = MutableLiveData<ExpenseBasketClass>()
    val expenseRecord: LiveData<ExpenseBasketClass> get() = _expenseRecord

    private val _expenseRecordTotalCost = MutableLiveData<Float>()
    val expenseRecordTotalCost: LiveData<Float>
        get() = _expenseRecordTotalCost

    private val _expenseForm = MutableLiveData<MutableList<String?>>()
    private val expenseForm: LiveData<MutableList<String?>>
        get() = _expenseForm

    private val expenseFormFields = mutableListOf<String?>()


    init {
        Timber.i("ReceiptViewModel created")
        _expenseRecord.value = expenseBasket

        _expenseForm.value = expenseFormFields

    }

    private fun clearExpenseForm() {
        _description.value = ""
        _costText.value = ""
        _cost.value = 0F
        _costFormat.value = ""
        _quantityText.value = ""
        _quantity.value = 0
        _subCost.value = 0F
        _subCostFormat.value = ""
        _essentialRating.value = 0
        _error.value = null
        expenseFormFields.removeAll(expenseFormFields)
    }

    val _expenseBottomSheet = MutableLiveData<BottomSheetClass>()
    val expenseBottomSheet: LiveData<BottomSheetClass>
        get() = _expenseBottomSheet

    fun setExitExpenseOnClick() {

        _expenseBottomSheet.value!!.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        clearExpenseForm()

    }



    fun validateExpenseInputs() {

        expenseFormFields.add(_description.value)
        expenseFormFields.add(_costText.value)
        expenseFormFields.add(_quantityText.value)

        var nullDetected = true

        if (expenseFormFields.contains(null) ||
            expenseFormFields[0]!!.isBlank() ||
            expenseFormFields[0]!!.isEmpty() ||
            expenseFormFields[1]!! == "0.00" ||
            expenseFormFields[2]!! == "0" ||
            expenseFormFields[2]!! == "00" ||
            expenseFormFields[2]!! == "000" ||
            expenseFormFields[2]!!.isNullOrBlank()
        ) {
            _error.value = ErrorMessages.KEY_ERROR_GENERAL.errorMessage

            expenseFormFields.removeAll(expenseFormFields)

        } else {

            _error.value = null
            nullDetected = false

            expenseFormFields.removeAll(expenseFormFields)

        }

        when (nullDetected) {
            true -> _message.value = EventClass(ErrorMessages.KEY_ERROR_GENERAL.errorMessage)
            false -> createExpense()
        }

    }


    fun createExpense() {

        val trimmedDescription = _description.value!!.trim()

        _description.value = trimmedDescription

        // Formats the UserInput text into an Int value for future calculations
        _quantity.value = _quantityText.value?.toInt() ?: 0

        // Formats the UserInput text into a Float value for future calculations
        val filteredCostText = _costText.value!!.replace(",", "")

        _cost.value = filteredCostText.toFloat() ?: 0F

        _costFormat.value = "%.2f".format(_cost.value)

        // Processes the subCost value before it gets bundled into an Expense object
        _subCost.value = _cost.value?.times(_quantity.value!!) ?: 0F

        _subCostFormat.value = "%.2f".format(_subCost.value)

        expense = ExpenseClass(description, cost, costFormat, quantity, subCost, subCostFormat, essentialRating)

        expenseBasket.expenseList.add(expense)

        _message.value = EventClass("Expense: ${expense.description.value} | Cost: $${expense.subCostFormat.value} added!")

        clearExpenseForm()

        Timber.i("Expense form is cleared")
        Timber.i("Expense Basket contains: ${expenseBasket.expenseList.size} items after user input")
    }



    val _patronBottomSheet = MutableLiveData<BottomSheetClass>()
    val patronBottomSheet: LiveData<BottomSheetClass>
        get() = _patronBottomSheet

    fun setExitPatronOnClick() {

        _patronBottomSheet.value!!.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        clearPatronForm()

    }

    private fun clearPatronForm() {
        _firstName.value = ""
        _lastName.value = ""
    }



    /* Below is the code logic for creating a Patron object through the PatronClass
     * Variables are affected by userinput within the bottom_sheet_patron.xml
     */
    val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String>
        get() = _firstName

    val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String>
        get() = _lastName

    private val _patronID = MutableLiveData<Int>()
    val patronID: LiveData<Int>
        get() = _patronID

    private val _cart = MutableLiveData<MutableList<ExpenseClass>>()
    val cart: LiveData<MutableList<ExpenseClass>>
        get() = _cart

    lateinit var patron: PatronClass

    fun createPatron() {

        // TODO (03) Clarify code logic for setting patronID, and setting up the cart variable

        patron = PatronClass(firstName, lastName, patronID, cart)

        // TODO (05) Create code logic to clear out UserInput fields after "adding"

        Timber.i("Patron: ${patron.firstName.value}, ${patron.lastName.value} is created")
    }



    private var _patronList = MutableLiveData<MutableList<PatronClass>>()
    val patronList: LiveData<MutableList<PatronClass>>
        get() = _patronList



    // TODO (04) Create code logic for storing UserInput Expenses and Patrons into MutableLists
    // TODO (07) Figure out how to extract this data to transfer to ReceiptSummaryFragment





}