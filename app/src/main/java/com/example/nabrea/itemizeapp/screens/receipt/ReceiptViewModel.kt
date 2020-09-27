package com.example.nabrea.itemizeapp.screens.receipt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nabrea.itemizeapp.EventClass
import com.example.nabrea.itemizeapp.database.ExpenseDao
import com.example.nabrea.itemizeapp.database.PatronDao
import com.example.nabrea.itemizeapp.database.ReceiptDatabase
import com.example.nabrea.itemizeapp.database.ReceiptRepository
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseBasketClass
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.BottomSheetClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ReceiptViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReceiptRepository

    private val expenseDao: ExpenseDao

    private val patronDao: PatronDao

    val allExpenses: LiveData<List<ExpenseDataClass>>

    val allPatrons: LiveData<List<PatronDataClass>>

    private var _message = MutableLiveData<EventClass<String>>()
    val message: LiveData<EventClass<String>>
        get() = _message

    private var _errorExpense = MutableLiveData<String>()
    val errorExpense: LiveData<String>
        get() = _errorExpense

    private var _errorPatron = MutableLiveData<String>()
    val errorPatron: LiveData<String>
        get() = _errorPatron

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

    lateinit var expense: ExpenseDataClass

    val expenseList: MutableList<ExpenseDataClass> = mutableListOf()

    // Below is the code logic for compiling UserInput for Expenses and Patrons
    val expenseBasket = ExpenseBasketClass(expenseList)

    private val _expenseRecord = MutableLiveData<ExpenseBasketClass>()
    val expenseRecord: LiveData<ExpenseBasketClass> get() = _expenseRecord

    private val _expenseRecordTotalCost = MutableLiveData<Float>()
    val expenseRecordTotalCost: LiveData<Float>
        get() = _expenseRecordTotalCost

    private val _expenseForm = MutableLiveData<MutableList<String?>>()

    private val expenseFormFields = mutableListOf<String?>()

    private val _patronForm = MutableLiveData<MutableList<String?>>()

    private val patronFormFields = mutableListOf<String?>()

    init {
        Timber.i("ReceiptViewModel created")

        expenseDao =
            ReceiptDatabase.getDatabase(application, viewModelScope).expenseDao()

        patronDao =
            ReceiptDatabase.getDatabase(application, viewModelScope).patronDao()

        repository = ReceiptRepository(expenseDao, patronDao)

        allExpenses = repository.allExpenses

        allPatrons = repository.allPatrons

        deleteAll()

        _expenseRecord.value = expenseBasket

        _expenseForm.value = expenseFormFields

        _patronForm.value = patronFormFields
    }

    private fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.deleteAllExpenses()
        patronDao.deleteAllPatrons()
    }

    private fun insertExpense(expense: ExpenseDataClass) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExpense(expense)
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
        _errorExpense.value = null
        expenseFormFields.removeAll(expenseFormFields)
    }

    val _expenseBottomSheet = MutableLiveData<BottomSheetClass>()
    val expenseBottomSheet: LiveData<BottomSheetClass>
        get() = _expenseBottomSheet

    fun setExitExpenseOnClick() {

        clearExpenseForm()

        _expenseBottomSheet.value!!.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

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
            _errorExpense.value = ErrorMessages.KEY_ERROR_GENERAL.errorMessage

            expenseFormFields.removeAll(expenseFormFields)

        } else {

            _errorExpense.value = null
            nullDetected = false

            expenseFormFields.removeAll(expenseFormFields)

        }

        when (nullDetected) {
            true -> _message.value = EventClass(ErrorMessages.KEY_ERROR_GENERAL.errorMessage)
            false -> createExpense()
        }

    }


    private fun createExpense() {

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

        expense = ExpenseDataClass(
                description.value.toString(),
                cost.value!!.toFloat(),
                costFormat.value!!.toString(),
                quantity.value!!.toInt(),
                subCost.value!!.toFloat(),
                subCostFormat.value.toString(),
                essentialRating.value
            )

        /*expenseBasket.expenseList.add(expense)*/
        insertExpense(expense)

        _message.value = EventClass(
            "Expense: ${expense.description} | Cost: $${expense.subCostFormat} added!"
        )

        clearExpenseForm()

        Timber.i("Expense form is cleared")

        Timber.i("Expense Basket contains: ${expenseBasket.expenseList.size} items after user input")
    }






    /* Below is the code logic for creating a Patron object through the PatronClass
     * Variables are affected by userinput within the bottom_sheet_patron.xml
     */
    private val _patronID = MutableLiveData<Int>()
    val patronID: LiveData<Int>
        get() = _patronID

    val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _nameInitials = MutableLiveData<String>()
    val nameInitials: LiveData<String>
        get() = _nameInitials

    /*val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String>
        get() = _lastName*/

    private val _cart = MutableLiveData<MutableList<ExpenseDataClass>>()
    val cart: LiveData<MutableList<ExpenseDataClass>>
        get() = _cart

    lateinit var patron: PatronDataClass

    val _patronBottomSheet = MutableLiveData<BottomSheetClass>()

    val patronBottomSheet: LiveData<BottomSheetClass>
        get() = _patronBottomSheet



    fun setExitPatronOnClick() {

        _patronBottomSheet.value!!.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        clearPatronForm()

    }

    private fun clearPatronForm() {

        _name.value = ""
        _nameInitials.value = ""
        _cart.value = null
        _errorPatron.value = null

        /*_lastName.value = ""*/
    }


    fun validatePatronInputs() {

        patronFormFields.add(_name.value)

        var nullDetected = true

        if (patronFormFields.contains(null) ||
            patronFormFields[0]!!.isBlank() ||
            patronFormFields[0]!!.isEmpty()
        ) {
            _errorPatron.value = ErrorMessages.KEY_ERROR_GENERAL.errorMessage

            patronFormFields.removeAll(patronFormFields)

        } else {

            _errorPatron.value = null
            nullDetected = false

            patronFormFields.removeAll(patronFormFields)

        }

        when (nullDetected) {
            true -> _message.value = EventClass(ErrorMessages.KEY_ERROR_GENERAL.errorMessage)
            false -> createPatron()
        }

    }

    private fun insertPatron(patron: PatronDataClass) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPatron(patron)
        }



    private fun createPatron() {

        _cart.value = mutableListOf()


        getPatronInitials()

        patron = PatronDataClass(
            0L,
            name.value.toString(),
            /*lastName,*/
            nameInitials.value.toString()
        )

        insertPatron(patron)


        _message.value = EventClass(
            "Patron: ${patron.name} added!"
        )

        clearPatronForm()

        Timber.i("Patron: ${patron.name}, ${patron.nameInitials} is created")
    }

    private fun getPatronInitials() {

        val tempList = mutableListOf<String>()

        val userInput = _name.value

        val split = userInput!!.split(" ")

        for (i in 0..1) {
            val firstLetter = split[i].substring(0,1)
            tempList.add(firstLetter)
        }

        _nameInitials.value = tempList.joinToString(separator = "") { it }

        Timber.i("${nameInitials.value} is created")

    }



    private var _patronList = MutableLiveData<MutableList<PatronDataClass>>()
    val patronList: LiveData<MutableList<PatronDataClass>>
        get() = _patronList


    override fun onCleared() {
        super.onCleared()

        Timber.i("onCleared() is called. ViewModel instance is cleared.")
    }





}