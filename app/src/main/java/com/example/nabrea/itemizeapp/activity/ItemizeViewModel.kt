package com.example.nabrea.itemizeapp.activity

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
import com.example.nabrea.itemizeapp.screens.receipt.ErrorMessages
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.BottomSheetClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ItemizeViewModel(application: Application) : AndroidViewModel(application) {

    // Variable for establishing connection with the repository associated with the Receipt Fragment
    private val repository: ReceiptRepository

    // Variable for establishing connection with the expensedao
    private val expenseDao: ExpenseDao

    // Variable for establishing connection with the patrondao
    private val patronDao: PatronDao

    private var viewModelJob = Job()

    // Variable for locating the variable where the expense information is stored
    val allExpenses: LiveData<List<ExpenseDataClass>>

    // Variable for locating the variable where the patron information is stored
    val allPatrons: LiveData<List<PatronDataClass>>

    // Variable that informs the program of any messages for user feedback
    val _message = MutableLiveData<EventClass<String>>()
    val message: LiveData<EventClass<String>>
        get() = _message

    // Variable that informs the program of any errors with the expense form
    private val _errorExpense = MutableLiveData<String>()
    val errorExpense: LiveData<String>
        get() = _errorExpense

    // Variable that informs the program of any errors with the patron form
    private val _errorPatron = MutableLiveData<String>()
    val errorPatron: LiveData<String>
        get() = _errorPatron

    // Variable that stores the user input for the Expense description
    val _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    // Variable that stores the user input for the Expense cost
    var _costText = MutableLiveData<String>()

    // Variable that formats the _costText variable
    private val _costFormat = MutableLiveData<String>()
    val costFormat: LiveData<String>
        get() = _costFormat

    // Variable that stores the Float value of the _costText variable
    private val _cost = MutableLiveData<Float>()
    val cost: LiveData<Float>
        get() = _cost

    // Variable that stores the user input for the Expense quantity
    val _quantityText = MutableLiveData<String>()

    private val _quantityFormat = MutableLiveData<String>()
    val quantityFormat: LiveData<String>
        get() = _quantityFormat

    // Variables that stores the Int value of the _quantityText variable
    private val _quantity = MutableLiveData<Float>()
    val quantity: LiveData<Float>
        get() = _quantity

    // Variable that stores the calculated Float value of sub cost based on cost and quantity input
    private val _subCost = MutableLiveData<Float>()
    val subCost: LiveData<Float>
        get() = _subCost

    // Variable that stores the formatted value of the calculated sub cost.
    private val _subCostFormat =  MutableLiveData<String>()
    val subCostFormat: LiveData<String>
        get() = _subCostFormat

    // Variable that stores the essentialrating for an expense.
    private val _essentialRating = MutableLiveData<Int>()
    val essentialRating: LiveData<Int>
        get() = _essentialRating

    // Variable that collects the user input information into an ExpenseDataClass to be stored in the database
    lateinit var expense: ExpenseDataClass

    // Variable that temporarily collects the user input text expense fields for input validation
    private val expenseFormFields = mutableListOf<String?>()

    // Variable that temporarily collects the user input text patron fields for input validation
    private val patronFormFields = mutableListOf<String?>()

    val _receiptTotal = MutableLiveData<Float>()
    val receiptTotal: LiveData<Float>
        get() = _receiptTotal

    val _receiptTotalText = MutableLiveData<String>()

    var isNew: Boolean = true

    private val _isUpdated = MutableLiveData<Boolean>()
    val isUpdated: LiveData<Boolean>
        get() = _isUpdated

    init {
        Timber.i("ReceiptViewModel created")

        // Establishing connection to the expensedao from the database
        expenseDao =
            ReceiptDatabase.getDatabase(application, viewModelScope).expenseDao()

        // Establishing connection to the patrondao from the database
        patronDao =
            ReceiptDatabase.getDatabase(application, viewModelScope).patronDao()

        // Establishing the repository that links to both the expensedao and the patrondao
        repository = ReceiptRepository(expenseDao, patronDao)

        // Connecting to the database method that pulls all expense data
        allExpenses = repository.allExpenses

        // Connecting to the database method that pulls all patron data
        allPatrons = repository.allPatrons

        // Method deletes all database information everytime the ViewModel is created or recreated.
        deleteAll()

        _receiptTotal.value = 0F
    }

    private suspend fun insertExpense(expense: ExpenseDataClass) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExpense(expense)
        }
    }



    fun deleteExpense(swipedExpense: ExpenseDataClass) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExpense(swipedExpense)
    }



    // Method for deleting all database information
    private fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.deleteAllExpenses()
        patronDao.deleteAllPatrons()
    }



    // Method for cleaning out the expense input fields and any errors
    fun clearNewExpenseForm() {
        Timber.i("ItemizeViewModel clearExpenseForm() is called")

        _description.value = ""
        _costText.value = ""
        _cost.value = 0F
        _costFormat.value = ""
        _quantityText.value = ""
        _quantity.value = 0F
        _quantityFormat.value = ""
        _subCost.value = 0F
        _subCostFormat.value = ""
        _essentialRating.value = 0
        _errorExpense.value = null
        expenseFormFields.removeAll(expenseFormFields)
    }



    // Method for cleaning out the expense input fields and any errors
    fun clearUpdateExpenseForm() {
        Timber.i("ItemizeViewModel clearExpenseForm() is called")

        _updateDescription.value = ""
        _updateCostText.value = ""
        _updateCost.value = 0F
        _updateCostFormat.value = ""
        _updateQuantityText.value = ""
        _updateQuantity.value = 0F
        _updateQuantityFormat.value = ""
        _subCost.value = 0F
        _subCostFormat.value = ""
        _essentialRating.value = 0
        _errorExpense.value = null
        expenseFormFields.removeAll(expenseFormFields)
    }



    // ViewModel variable for storing the ExpenseBottomSheet state
    val _expenseBottomSheet = MutableLiveData<BottomSheetClass>()
    val expenseBottomSheet: LiveData<BottomSheetClass>
        get() = _expenseBottomSheet



    // Method for a chain of actions that happen once the expense bottom sheet form is exited
    fun setExitExpenseOnClick() {

        // Expense form is cleared
        clearNewExpenseForm()

        // BottomSheetState is collapsed
        _expenseBottomSheet.value!!.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }



    fun notifyExpenseUpdate(descriptionInput: MutableLiveData<String>,
                            costInput: MutableLiveData<String>,
                            quantityInput:MutableLiveData<String>
    ) {
        isNew = false

        _isUpdated.value = false

        validateExpenseInputs(descriptionInput, costInput, quantityInput)
    }



    // Method for validating the user input expense fields
    fun validateExpenseInputs(
        descriptionInput: MutableLiveData<String>,
        costInput: MutableLiveData<String>,
        quantityInput:MutableLiveData<String>
    ) {

        // All ViewModel variables associated with user input are added to the formFields collection
        expenseFormFields.add(descriptionInput.value)
        expenseFormFields.add(costInput.value)
        expenseFormFields.add(quantityInput.value)

        // Default state of all forms are null
        var nullDetected = true

        // Setting the specific criteria for each of the form fields
        if (expenseFormFields.contains(null) ||
            expenseFormFields[0]!!.isBlank() ||
            expenseFormFields[0]!!.isEmpty() ||
            expenseFormFields[1]!! == "0.00" ||
            expenseFormFields[1]!!.isEmpty() ||
            expenseFormFields[1]!!.isBlank() ||
            expenseFormFields[2]!!.isEmpty() ||
            expenseFormFields[2]!! == "0.00" ||
            expenseFormFields[2]!!.isBlank()
        ) {
            // If any of the fields meet the criteria specified above, there's an error
            _errorExpense.value = ErrorMessages.KEY_ERROR_GENERAL.errorMessage

            // The temporary collection is cleaned out
            expenseFormFields.removeAll(expenseFormFields)

        } else {

            // If all of the fields meet criteria, the error message is cleared.
            _errorExpense.value = null

            // Input is cleared and therefore there are no nulls
            nullDetected = false

            // The temporary collection is cleaned out
            expenseFormFields.removeAll(expenseFormFields)

        }

        // Actions based on nullDetected value
        when (nullDetected) {

            // When nullDetected is true, inform the program that there is an error
            true -> _message.value = EventClass(ErrorMessages.KEY_ERROR_GENERAL.errorMessage)

            // When nullDetected is false, allow the program to create the expense
            false -> createExpense(descriptionInput, costInput, quantityInput)
        }

    }



    // Method for creating an expense based on the user input
    private fun createExpense(
        descriptionInput: MutableLiveData<String>,
        costInput: MutableLiveData<String>,
        quantityInput:MutableLiveData<String>
    ) {

        // Variable with a trimmed format in case the user puts in excessive spaces
        val trimmedDescription = descriptionInput.value!!.trim()

        // Formats the UserInput text into a Float value for future calculations
        val filteredCostText = costInput.value!!.replace(",", "")

        when (isNew) {
            true -> {

                // Updating the stored input with the formatted input
                _description.value = trimmedDescription

                // Formats the UserInput text into an Int value for future calculations
                _quantity.value = quantityInput.value?.toFloat() ?: 0F

                _quantityFormat.value = "%.2f".format(_quantity.value)

                // Formally storing the user input cost as a Float value for calculations
                _cost.value = filteredCostText.toFloat()

                // Formally storing a formatted version of the cost into a readable format
                _costFormat.value = "%.2f".format(_cost.value)

                // Processes the subCost value before it gets bundled into an Expense object
                _subCost.value = _cost.value?.times(_quantity.value!!) ?: 0F

                // Formally storing a formatted version of the subcost into a readable format
                _subCostFormat.value = "%.2f".format(_subCost.value)

                // Creating an expense based on the processed user input.
                expense = ExpenseDataClass(
                    null,
                    description.value.toString(),
                    cost.value!!.toFloat(),
                    costFormat.value!!.toString(),
                    quantity.value!!.toFloat(),
                    quantityFormat.value!!.toString(),
                    subCost.value!!.toFloat(),
                    subCostFormat.value.toString(),
                    essentialRating.value
                )



                viewModelScope.launch {
                    insertExpense(expense)
                    Timber.i("New expense ${expense.expenseId} is inserted")
                }

                // Informing the program that an expense was created for user feedback.
                _message.value = EventClass(
                    "${expense.description} | Cost: $${expense.subCostFormat} added!"
                )



                // ExpenseForm is cleared once stored in the database
                clearNewExpenseForm()
            }
            false -> {

                // Updating the stored input with the formatted input
                _updateDescription.value = trimmedDescription

                // Formats the UserInput text into an Int value for future calculations
                _updateQuantity.value = quantityInput.value?.toFloat() ?: 0F

                _updateQuantityFormat.value = "%.2f".format(_updateQuantity.value)

                // Formally storing the user input cost as a Float value for calculations
                _updateCost.value = filteredCostText.toFloat()

                // Formally storing a formatted version of the cost into a readable format
                _updateCostFormat.value = "%.2f".format(_updateCost.value)

                // Processes the subCost value before it gets bundled into an Expense object
                _subCost.value = _updateCost.value?.times(_updateQuantity.value!!) ?: 0F

                // Formally storing a formatted version of the subcost into a readable format
                _subCostFormat.value = "%.2f".format(_subCost.value)

                expense = ExpenseDataClass(
                    updateExpenseId.value,
                    updateDescription.value.toString(),
                    updateCost.value!!.toFloat(),
                    updateCostFormat.value!!.toString(),
                    updateQuantity.value!!.toFloat(),
                    updateQuantityFormat.value!!.toString(),
                    subCost.value!!.toFloat(),
                    subCostFormat.value.toString(),
                    essentialRating.value
                )



                viewModelScope.launch {
                    updateExpense(expense)
                    Timber.i("Current expense ${expense.expenseId} is updated")
                }

                // Informing the program that an expense was created for user feedback.
                _message.value = EventClass(
                    "Expense has been updated!"
                )



                // ExpenseForm is cleared once stored in the database
                clearUpdateExpenseForm()

                isNew = true

                _isUpdated.value = true
            }

        }

        _isUpdated.value = false
        /*val newTotal = _receiptTotal.value!!.plus(subCost.value!!.toFloat())

        _receiptTotal.value = newTotal*/

        // Logcat message to confirm the expense was processed
        Timber.i("Expense was processed, form is cleared")

    }



    val _updateExpenseId = MutableLiveData<Long>()
    val updateExpenseId: LiveData<Long>
        get() = _updateExpenseId

    val _updateDescription = MutableLiveData<String>()
    val updateDescription: LiveData<String>
        get() = _updateDescription

    val _currentDescription = MutableLiveData<String>()

    val _updateCostText = MutableLiveData<String>()

    val _currentCostText = MutableLiveData<String>()

    private val _updateCost = MutableLiveData<Float>()
    val updateCost: LiveData<Float>
        get() = _updateCost

    private val _updateCostFormat = MutableLiveData<String>()
    val updateCostFormat: LiveData<String>
        get() = _updateCostFormat

    val _updateQuantityText = MutableLiveData<String>()

    val _currentQuantityText = MutableLiveData<String>()

    private val _updateQuantityFormat = MutableLiveData<String>()
    val updateQuantityFormat: LiveData<String>
        get() = _updateQuantityFormat

    private val _updateQuantity = MutableLiveData<Float>()
    val updateQuantity: LiveData<Float>
        get() = _updateQuantity

    private suspend fun updateExpense(expense: ExpenseDataClass) =
        viewModelScope.launch(Dispatchers.IO) {

            repository.updateExpense(expense)

        }




    /* Below is the code logic for creating a Patron object through the PatronClass
     * Variables are affected by userinput within the bottom_sheet_patron.xml
     */
    // Variable for storing the patron name user input
    val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    // Variable for storing the patron initials based on user input. Used for recyclerview display
    private val _nameInitials = MutableLiveData<String>()
    val nameInitials: LiveData<String>
        get() = _nameInitials

    /*private val _cart = MutableLiveData<MutableList<ExpenseDataClass>>()
    val cart: LiveData<MutableList<ExpenseDataClass>>
        get() = _cart
    */

    // Variable for the PatronDataClass
    lateinit var patron: PatronDataClass

    // Variable for patronBottomSheet state
    val _patronBottomSheet = MutableLiveData<BottomSheetClass>()
    val patronBottomSheet: LiveData<BottomSheetClass>
        get() = _patronBottomSheet



    // Method for setting a chain of actions once the PatronBottomSheet is collapsed
    fun setExitPatronOnClick() {

        // Informing the Fragment that the BottomSheet should be collapsed
        _patronBottomSheet.value!!.bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Once the patron bottom sheet is collapsed, the form is entirely cleared
        clearNewPatronForm()
    }



    // Method for clearing out the patron form
    fun clearNewPatronForm() {
        Timber.i("ItemizeViewModel clearPatronForm() is called")

        _name.value = ""
        _nameInitials.value = ""
        /*_cart.value = null*/
        _errorPatron.value = null

    }



    // Method for clearing out the patron form
    fun clearUpdatePatronForm() {
        Timber.i("ItemizeViewModel clearPatronForm() is called")

        _name.value = ""
        _nameInitials.value = ""
        /*_cart.value = null*/
        _errorPatron.value = null

    }



    suspend fun deleteSelectedPatron(patron: PatronDataClass) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePatron(patron)
        }
    }



    val _updatePatronID = MutableLiveData<Long>()
    val updatePatronID: LiveData<Long>
        get() = _updatePatronID

    val _updatePatronName = MutableLiveData<String>()
    val updatePatronName: LiveData<String>
        get() = _updatePatronName

    val _currentPatronName = MutableLiveData<String>()
    val currentPatronName: LiveData<String>
        get() = _currentPatronName

    private suspend fun updatePatron(patron: PatronDataClass) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePatron(patron)
        }
    }

    fun notifyPatronUpdate(nameInput: MutableLiveData<String>) {

        isNew = false

        _isUpdated.value = false

        validatePatronInputs(nameInput)

    }



    // Method for validating the Patron user input fields
    fun validatePatronInputs(nameInput: MutableLiveData<String>) {

        // Pulling out the userinput value
        val userInput = nameInput.value!!.trim()

        // Variables are temporarily collected into the formFields variable
        patronFormFields.add(userInput)

        // By default the form entries are null
        var nullDetected = true

        // Below is the criteria for the patron form fields
        if (patronFormFields.contains(null) ||
            patronFormFields[0]!!.isBlank() ||
            patronFormFields[0]!!.isEmpty() ||
            patronFormFields[0]!!.split(" ").size <= 1
        ) {
            // If any of the above listed criteria applies, inform the program and user that there is an error
            _errorPatron.value = ErrorMessages.KEY_ERROR_GENERAL.errorMessage

            // The temporary collection is cleared out.
            patronFormFields.removeAll(patronFormFields)

        } else {

            // If none of the listed criteria is present, there is no error found.
            _errorPatron.value = null

            // Entry is not null
            nullDetected = false

            // The temporary collection is cleared out
            patronFormFields.removeAll(patronFormFields)

        }

        // Actions to take based on the nullDetected variable
        when (nullDetected) {

            // When nullDetected is true, inform the user and program that there is an error present
            true -> _message.value = EventClass(ErrorMessages.KEY_ERROR_GENERAL.errorMessage)

            // When nullDetected is false, allow the user to create the patron.
            false -> createPatron(nameInput)
        }
    }



    // Method for inserting the patron into the database
    private suspend fun insertPatron(patron: PatronDataClass) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPatron(patron)
        }



    // Method for creating a patron variable
    private fun createPatron(nameInput: MutableLiveData<String>) {

        /*_cart.value = mutableListOf()*/

        // Method for processing the patron's initials based on user input.
        getPatronInitials(nameInput)

        val userInput = nameInput.value!!.trim()

        when (isNew) {
            true -> {
                _name.value = userInput

                patron = PatronDataClass(
                    null,
                    name.value.toString(),
                    nameInitials.value.toString()
                )

                viewModelScope.launch {
                    // Method for inserting the patron into the database
                    insertPatron(patron)
                }

                // Displaying user feedback that the patron has been stored in the database
                _message.value = EventClass(
                    "Patron: ${patron.name} added!"
                )

                // User input fields are cleared
                clearNewPatronForm()

                // Logcat confirmation that the user input has been processed
                Timber.i("Patron: ${patron.name}, ${patron.nameInitials} is created")

            }
            false-> {

                _updatePatronName.value = userInput

                // Collecting the user input into the patron data class
                patron = PatronDataClass(
                    updatePatronID.value,
                    updatePatronName.value.toString(),
                    nameInitials.value.toString()
                )

                viewModelScope.launch {
                    updatePatron(patron)
                }

                clearUpdatePatronForm()

                isNew = true

                _isUpdated.value = true
            }
        }

        _isUpdated.value = false
        /*val newTotal = _receiptTotal.value!!.plus(subCost.value!!.toFloat())

        _receiptTotal.value = newTotal*/

        // Logcat message to confirm the expense was processed
        Timber.i("Patron was processed, form is cleared")
    }



    // Method for processing initials from the user input
    private fun getPatronInitials(nameInput: MutableLiveData<String>) {

        // Variable to temporarily collect processed characters as strings
        val tempList = mutableListOf<String>()

        // Pulling out the userinput value
        val userInput = nameInput.value!!.trim()

        // Splitting the user input into separate strings
        val split = userInput.split(" ")

        // Taking the first letter from the first two strings
        for (i in 0..1) {
            val firstLetter = split[i].trim().substring(0,1)
            tempList.add(firstLetter)
        }

        // Storing the first letters of these strings as one string
        _nameInitials.value = tempList.joinToString(separator = "") { it }

        // Internal logcat confirmation message
        Timber.i("${nameInitials.value} is created")

    }



    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()

        Timber.i("onCleared() is called. ViewModel instance is cleared.")
    }
}