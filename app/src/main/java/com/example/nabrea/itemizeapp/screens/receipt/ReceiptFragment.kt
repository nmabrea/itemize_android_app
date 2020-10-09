package com.example.nabrea.itemizeapp.screens.receipt

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.ExpandingFabAnimationInterface
import com.example.nabrea.itemizeapp.ItemizeTextWatcherClass
import com.example.nabrea.itemizeapp.ItemizeTouchHelperClass
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.activity.ItemizeViewModel
import com.example.nabrea.itemizeapp.database.ExpenseListAdapter
import com.example.nabrea.itemizeapp.database.PatronListAdapter
import com.example.nabrea.itemizeapp.databinding.FragmentReceiptBinding
import com.example.nabrea.itemizeapp.screens.receipt.expense.UpdateExpenseDialogFragment
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass
import com.example.nabrea.itemizeapp.screens.receipt.patron.UpdatePatronDialogFragment
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.BottomSheetClass
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.MaterialDatePickerClass
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.MenuClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

// Small constant values to be saved within onSavedInstanceState() below.
// Constant value for the Date Selected via MaterialDatePicker in the Receipt Fragment
const val KEY_DATE: String = "key_date"

// Constant value for the UserInput StoreName via EditTextField in the Receipt Fragment
const val KEY_STORE: String = "key_store"

// Enum class storing error typical error messages that the user may encounter
enum class ErrorMessages (val errorMessage: String) {

    KEY_ERROR_GENERAL("Please fill in all fields correctly."),
    KEY_ERROR_DESCRIPTION("Enter a description. (20 characters max)"),
    KEY_ERROR_COST("Enter a cost. (8 digits max)"),
    KEY_ERROR_QUANTITY("Enter a quantity. (3 digits max)"),
    KEY_ERROR_NAME("Enter a first + last name. (20 characters max)")

}


// Interface to be used as communication between an instance of this Fragment and the MainActivity
interface ReceiptFragmentCommunication {

    // Function to be called when a BottomSheet is STATE_COLLAPSED
    fun onBottomSheetCollapsed()

    // Function to be called when a BottomSheet is STATE_EXPANDED
    fun onBottomSheetExpanded()

    // Function to be called when Activity needs to display ReceiptFragment's menu actions
    fun setMenuAwareness(menu: MenuClass, background: LinearLayoutCompat, host: ReceiptFragment)

    // Function used to hide the keyboard from the user
    fun hideKeyboard(view: View)

    // Function used to display a snackbar for user feedback
    fun displaySnackbar(message: String)

    // Function used to hide/show bottomappbar and fab
    fun setNavigationScrollVisibility(
        recyclerView: RecyclerView,
        expenseBottomSheet: BottomSheetClass,
        patronBottomSheet: BottomSheetClass
    )

}

// Receipt Fragment has mini FAB that act as an expandable menu. It controls the animations for these buttons.
class ReceiptFragment : Fragment(),
    ExpandingFabAnimationInterface {

    // Variable to establish communication with the Main Activity as the listener
    lateinit var listener: ReceiptFragmentCommunication

    // DataBinding variable for this Fragment
    private lateinit var receiptBinding: FragmentReceiptBinding

    // ViewModel associated with this Fragment
    private lateinit var receiptVm: ItemizeViewModel

    // Adapter for the expense recycler view
    private lateinit var expenseAdapter: ExpenseListAdapter

    // Adapter for the patron recycler view
    private lateinit var patronAdapter: PatronListAdapter

    // An instance of the MenuClass. Takes in menu options along with the primaryButton
    private lateinit var expandMenu: MenuClass

    // Variable for the AddExpense Button
    private lateinit var expenseButton: FloatingActionButton

    // Variable for the AddExpense Button's associated label
    private lateinit var expenseLabel: MaterialTextView

    // Variable for the Layout view associated with the AddExpense Button
    private lateinit var expenseLayout: ConstraintLayout

    // BottomSheetClass variable that passes in the Layout associated with the AddExpense Button
    private lateinit var expenseBottomSheet: BottomSheetClass

    // Variable for the expense description TextInputLayout view
    private lateinit var expenseDescription: TextInputLayout

    // Variable for the expense description EditText view
    private lateinit var expenseDescriptionEditText: TextInputEditText

    // Variable for the expense cost TextInputLayout view
    private lateinit var expenseCost: TextInputLayout

    // Variable for the expense cost EditText view
    private lateinit var expenseCostEditText: TextInputEditText

    // Variable for the expense quantity TextInputLayout view
    private lateinit var expenseQuantity: TextInputLayout

    // Variable for the expense quantity EditText view
    private lateinit var expenseQuantityEditText: TextInputEditText

    // Variable that collects the views associated with expense information and their associated error messages.
    private lateinit var expenseForm: MutableMap<Pair<TextInputEditText, TextInputLayout>, String>

    // Variable for the AddPatron Button
    private lateinit var patronButton: FloatingActionButton

    // Variable for the AddPatron Button's associated label
    private lateinit var patronLabel: MaterialTextView

    // Variable for the Layout view associated with the AddPatron Button
    private lateinit var patronLayout: ConstraintLayout

    // BottomSheetClass variable that passes in the Layout associated with the AddPatron Button
    private lateinit var patronBottomSheet: BottomSheetClass

    // Variable for the Patron's name input, displays errors
    private lateinit var patronName: TextInputLayout

    // Variable for the Patron's name input, takes in the user input
    private lateinit var patronNameEditText: TextInputEditText

    // Variable that collects the views associated with patron information and their associated error messages.
    private lateinit var patronForm: MutableMap<Pair<TextInputEditText, TextInputLayout>, String>

    // Variable for the Finalize Button
    private lateinit var finalizeButton: FloatingActionButton

    // Variable for the Finalize Button's associated label
    private lateinit var finalizeLabel: MaterialTextView

    // NavDirections for the Finalize Button
    private lateinit var finalizeDirections: NavDirections

    // Instance of expandable menu items that appear with BottomSheetBehavior Actions
    private lateinit var menuSheetActions: MutableMap<
            Pair<FloatingActionButton, MaterialTextView>, BottomSheetClass>

    // Transparent background to appear when a BottomSheetBehavior is activated
    private lateinit var stateExpandedBackground: LinearLayoutCompat

    // NavController variable for navigation
    private lateinit var navController: NavController

    // Variable for establishing the FragmentManager
    private lateinit var fragManager: FragmentManager

    // Instance of expandable menu items that appear with Navigation Actions
    private lateinit var menuNavActions: MutableMap<
            Pair<FloatingActionButton, MaterialTextView>, NavDirections>



    private lateinit var editDate: TextInputLayout

    // The EditTextField associated with the DatePicker instance within the receipt_fragment.xml
    private lateinit var editDateText: TextInputEditText

    // The TextInputEditText field associated with the Store Name input.
    private lateinit var storeNameEdit: TextInputEditText

    private lateinit var receiptTotal: TextInputLayout

    private lateinit var receiptTotalEdit: TextInputEditText

    // Override values inherited from the ExpandingFabAnimation interface
    override lateinit var animationContext: Context

    private lateinit var receiptInformationButton: MaterialButton



    override fun onAttach(context: Context) {
        super.onAttach(context)

        // context: Context variable is the MainActivity() instance within the current session

        // Establishes the context as the ReceiptFragmentCommunication listener.
        listener = context as ReceiptFragmentCommunication

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Timber.i("onCreateView has been called")

        // Layout for this fragment is inflated using Data Binding
        receiptBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_receipt,
            container,
            false
        )

        // Below is the code sequence for the expandable FAB menu:
        // Setting the context for the FAB menu animations
        animationContext = receiptBinding.root.context

        // Getting the ReceiptViewModel
        receiptVm = ViewModelProvider(activity!!).get(ItemizeViewModel::class.java)

        // Associating the layout xml with the ViewModel for direct communication
        receiptBinding.itemizeViewModel = receiptVm

        // Identifying this Fragment as the lifecycleOwner of the ViewModel
        receiptBinding.lifecycleOwner = this

        return receiptBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("$listener is activated")



        // Establishing how to find the Patron RecyclerView from the layout
        val patronRecycler = receiptBinding.patronReceiptRecyclerView

        // Establishing this Fragment as the context to display the patron recyclerview
        patronAdapter = PatronListAdapter(animationContext, this)

        // Establishing the adapter to reference for the Recycler View
        patronRecycler.adapter = patronAdapter

        // Setting up an observer for the ViewModel variables that populate the Patron Recycler View
        receiptVm.allPatrons.observe(viewLifecycleOwner, { patrons ->

            patronAdapter.setPatrons(patrons)

        })



        // Navigation Options setup within the action bar for this Fragment
        setHasOptionsMenu(true)

        // NAVIGATION UI::Locating the NavController for Navigation
        navController = this.findNavController()

        fragManager = parentFragmentManager



        // Locating the text view storing the StoreName within the onSavedInstanceState()
        storeNameEdit = receiptBinding.receiptStoreNameEdit

        // Locating the EditText view for user Date selection
        editDateText = receiptBinding.receiptDateEdit

        editDate = receiptBinding.receiptDate

        receiptTotal = receiptBinding.receiptTotalCost

        receiptTotalEdit = receiptBinding.receiptTotalCostEdit

        receiptInformationButton = receiptBinding.receiptInfoButton



        // The code below locates the layout views associated with the expanding menu's expense button
        // Creating the expandMenu object through the custom ActionButton() class
        expandMenu = MenuClass(navController, animationContext)

        // Locating the expense button, and its associated label and bottom_sheet_expense.xml
        expenseButton = receiptBinding.buttonAddExpense

        // Locating the label text associated with the expense button
        expenseLabel = receiptBinding.labelAddExpense



        // The code below locates the layout views associated with the expense BottomSheet
        // Locating the expenseLayout, which is included as part of the receiptBinding
        expenseLayout = receiptBinding.expenseBottomSheet.expenseBottomSheetLayout

        // Identifying the transparent background to supplement BottomSheetBehavior actions
        stateExpandedBackground = receiptBinding.bottomSheetBackground.bottomSheetBackground

        // Setting up the background overlay to intercept all OnClick actions from items below.
        stateExpandedBackground.setOnClickListener { }

        // Creating an instance of the BottomSheetClass for the Expense Bottom Sheet
        expenseBottomSheet = BottomSheetClass(
            expenseLayout,
            stateExpandedBackground,
            listener,
            this,
            animationContext
        )



        // The code below locates the layout views for user input within the expense BottomSheet
        // Locating the expenseDescription InputLayout view from the layout
        expenseDescription = receiptBinding.expenseBottomSheet.expenseDescription

        // Locating the expenseDescription EditText view from the layout
        expenseDescriptionEditText = receiptBinding.expenseBottomSheet.expenseDescriptionEdit

        // Locating the expenseCost InputLayout view from the layout
        expenseCost = receiptBinding.expenseBottomSheet.expenseCost

        // Locating the expenseCost EditText view from the layout
        expenseCostEditText = receiptBinding.expenseBottomSheet.expenseCostEdit

        // Locating the expenseQuantity InputLayout view from the layout
        expenseQuantity = receiptBinding.expenseBottomSheet.expenseQuantity

        // Locating the expenseQuantity EditText view from the layout
        expenseQuantityEditText = receiptBinding.expenseBottomSheet.expenseQuantityEdit

        // This variable collects the expense text layout views as pairs, and associates them with a value from the EnumClass ErrorMessages
        expenseForm = mutableMapOf()

        expenseForm[Pair(expenseDescriptionEditText, expenseDescription)] =
            ErrorMessages.KEY_ERROR_DESCRIPTION.errorMessage

        expenseForm[Pair(expenseCostEditText, expenseCost)] =
            ErrorMessages.KEY_ERROR_COST.errorMessage

        expenseForm[Pair(expenseQuantityEditText, expenseQuantity)] =
            ErrorMessages.KEY_ERROR_QUANTITY.errorMessage



        // The code below locates the layout views associated with the patron button from the menu
        // Locating the patron button, and its associated label and bottom_sheet_patron.xml
        patronButton = receiptBinding.buttonAddPatron

        // Locating the label text associated with the patron button
        patronLabel = receiptBinding.labelAddPatron

        // The code below locates the layout views associated with the patron BottomSheet
        // Locating the patronLayout, which is included as part of receiptBinding
        patronLayout = receiptBinding.patronBottomSheet.patronBottomSheetLayout

        // Creating an instance of the BottomSheetClass for the Patron Bottom Sheet
        patronBottomSheet = BottomSheetClass(
            patronLayout,
            stateExpandedBackground,
            listener,
            this,
            animationContext
        )



        // Locating the patronName InputLayout view from the layout
        patronName = receiptBinding.patronBottomSheet.patronNameInput

        // Locating the patronName EditText view from the layout
        patronNameEditText = receiptBinding.patronBottomSheet.patronNameInputEdit

        // This variable collects the patron text layout views and associates them with a value from the EnumClass ErrorMessages
        patronForm = mutableMapOf()

        patronForm[Pair(patronNameEditText,patronName)] = ErrorMessages.KEY_ERROR_NAME.errorMessage



        // Establishing how to find the Expense RecyclerView from the layout
        val expenseRecycler = receiptBinding.expenseRecyclerView

        listener.setNavigationScrollVisibility(
            expenseRecycler,
            expenseBottomSheet,
            patronBottomSheet
        )

        // Establishing this Fragment as the context to display both the Expense RecyclerView and its nested Patron RecyclerView
        expenseAdapter =
            ExpenseListAdapter(
                animationContext,
                patronAdapter,
                this
            )

        // Establishing the adapter to reference for the RecyclerView
        expenseRecycler.adapter = expenseAdapter

        // Establishing the LinearLayoutManager's context
        expenseRecycler.layoutManager = LinearLayoutManager(animationContext)

        val swipeGestures =
            ItemTouchHelper(ItemizeTouchHelperClass(receiptVm, expenseAdapter, animationContext))

        // Setting up an observer for the ViewModel variables that populate the Expense Recycler View
        receiptVm.allExpenses.observe(viewLifecycleOwner, { expenses ->

            expenseAdapter.setExpenses(expenses)

            swipeGestures.attachToRecyclerView(expenseRecycler)

        })



        // Locating the finalize button, and its associated label and NavDirections.
        finalizeButton = receiptBinding.buttonFinalize

        // Locating the label text associated with the finalize button
        finalizeLabel = receiptBinding.labelFinalize

        // Instantializing the NavDirections associated with the Finalize button
        finalizeDirections = ReceiptFragmentDirections
            .actionReceiptFragmentToReceiptSummaryFragment()



        // Recording the current menu actions that are associated with BottomSheetBehavior
        // Future menu actions can be appended by following the format below
        menuSheetActions = expandMenu.menuSheetActions
        menuSheetActions[Pair(expenseButton, expenseLabel)] = expenseBottomSheet
        menuSheetActions[Pair(patronButton, patronLabel)] = patronBottomSheet

        // Recording the current menu actions that are associated with NavDirections
        // Future menu actions can be appended by following the format below
        menuNavActions = expandMenu.menuNavActions
        menuNavActions[Pair(finalizeButton, finalizeLabel)] = finalizeDirections

        // Passing on the instance of this menu to the current MainActivity() instance
        listener.setMenuAwareness(expandMenu, stateExpandedBackground, this)
    }





    override fun onResume() {
        super.onResume()
        Timber.i("onResume() has been called")



        // Establishing rules for when a user types in the StoreName
        storeNameEdit.filters = arrayOf(InputFilter { charSequence, i, i2, spanned, i3, i4 ->
            return@InputFilter charSequence.replace(Regex("[^a-zA-Z0-9 ]*"), "")
        })

        ItemizeTextWatcherClass().setStringTextWatcher(storeNameEdit)



        receiptVm._receiptTotal.observe(viewLifecycleOwner, { total ->
            receiptVm._receiptTotalText.value = "%.2f".format(total)
        })



        var isShowing = true

        receiptInformationButton.setOnClickListener {

            if (isShowing) {
                editDate.visibility = TextInputLayout.GONE
                receiptTotal.visibility = TextInputLayout.GONE

                receiptInformationButton.text = getString(R.string.info_show)

                isShowing = false
            } else {
                editDate.visibility = TextInputLayout.VISIBLE
                receiptTotal.visibility = TextInputLayout.VISIBLE

                receiptInformationButton.text = getString(R.string.info_hide)

                isShowing = true
            }
        }



        // Locating the expenseBottomSheet values from the ViewModel that affect the state within the Fragment
        receiptVm._expenseBottomSheet.value = expenseBottomSheet

        // Locating the ViewModel variables that affect the bottomsheet state within the Fragment
        receiptVm._patronBottomSheet.value = patronBottomSheet



        // Establishing rules for user input for the patronName view
        patronNameEditText.filters = arrayOf(InputFilter { charSequence, i, i2, spanned, i3, i4 ->
            return@InputFilter charSequence.replace(Regex("[^a-zA-Z ]*"), "")
        })

        ItemizeTextWatcherClass().setStringTextWatcher(patronNameEditText)



        // Setting up an observer if a message is initialized within the ViewModel
        receiptVm.message.observe(this, { message ->
            message.getContentIfNotHandled()?.let { content ->

                Timer("message delay", false).schedule(200) {

                    // Communicating to the Main Activity to display ViewModel's message as a snackbar
                    listener.displaySnackbar(content)

                }
            }
        })

        // Setting up an observer for ViewModel variables that will affect the ExpenseBottomSheet state
        receiptVm.expenseBottomSheet.observe(this, { eBottomSheet ->

            when (eBottomSheet.bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {

                // ViewModel variables inform whether the bottom sheet should close
                true -> {

                   expenseBottomSheet.bottomSheetBehavior.state =

                       BottomSheetBehavior.STATE_COLLAPSED

               }
            }
        })



        // Setting up an observer for ViewModel variables that will affect the PatronBottomSheet state
        receiptVm.patronBottomSheet.observe(this, { pBottomSheet ->
            when (pBottomSheet.bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {

                // ViewModel variables inform whether the bottom sheet should close
                true -> {

                    patronBottomSheet.bottomSheetBehavior.state =

                        BottomSheetBehavior.STATE_COLLAPSED

                }
            }
        })



        // Establishing rules for how text input works for the ExpenseCostEditText view
        ItemizeTextWatcherClass().setCurrencyTextWatcher(expenseCostEditText)

        ItemizeTextWatcherClass().setCurrencyTextWatcher(expenseQuantityEditText)



        // Setting input rules for when users type in the description
        expenseDescriptionEditText.filters = arrayOf(InputFilter { charSequence, i, i2, spanned, i3, i4 ->
            return@InputFilter charSequence.replace(Regex("[^a-zA-Z0-9 ]*"), "")
        })

        ItemizeTextWatcherClass().setStringTextWatcher(expenseDescriptionEditText)



        // Setting an observer for ViewModel variables that inform the program of errors with the expenseForm
        receiptVm.errorExpense.observe(this, { errorMessage ->

            // If the ViewModel variable is null, all of the associated error values for the views are null
            if (errorMessage == null) {
                expenseForm.forEach { inputField ->
                    inputField.key.second.error = null
                }
            } else {

                // If the ViewModel variable has an error message, all input fields are checked
                // and error messages will be displayed based on the validation method's criteria
                expenseForm.forEach { inputField ->

                    if (!isExpenseInputValid(inputField.key.first)) {
                        inputField.key.second.error = inputField.value
                    } else {
                        inputField.key.second.error = null
                    }
                }
            }
        })



        // Setting an observer for ViewModel variables that inform the program of errors with the patronForm
        receiptVm.errorPatron.observe(this, { errorMessage ->

            // If the ViewModel variable is null, all of the associated error values for the views are null
            if (errorMessage == null) {
                patronForm.forEach { inputField ->
                    inputField.key.second.error = null
                }

            } else {

                // If the ViewModel variable has an error message, all input fields are checked
                // and error messages will be displayed based on the validation method's criteria
                patronForm.forEach { inputField ->
                    if(!isPatronInputValid(inputField.key.first)) {
                        inputField.key.second.error = inputField.value
                    } else {
                        inputField.key.second.error = null
                    }
                }
            }
        })



        // Variables for setting up the MaterialDatePicker Dialog
        val datePicker = MaterialDatePickerClass(editDateText, fragManager)
        datePicker.setTitle()
        datePicker.clickActions(listener)

    }



    // Method for validation criteria for various requirements of the expense form
    private fun isExpenseInputValid(userInput: TextInputEditText) : Boolean {
        return if (userInput.text == null) {
            false
        } else if (userInput.text!!.isEmpty()) {
            false
        } else if (userInput.text!!.isBlank()) {
            false
        } else if (userInput.text!!.toString() == "0") {
            false
        } else if (
            userInput.text!!.toString() == "00" ||
            userInput.text!!.toString() == "000"
        ) {
            false
        } else userInput.text!!.toString() != "0.00"
    }



    // Method for validation criteria for various requirements of the patron form
    private fun isPatronInputValid(userInput: TextInputEditText) : Boolean {

        val userInputTrimmed = userInput.text!!.trim()

        return when {

            userInput.text.isNullOrBlank() -> { false }

            userInputTrimmed.split(" ").size <= 1 -> { false }

            else -> userInput.text!!.isNotEmpty()
        }
    }



    // Method for reactivating the input forms once the bottom sheet is collapsed.
    fun setExpenseBottomSheetUnfocused() {
        // EditTextView is enabled for MaterialDatePicker
        editDateText.isClickable = true
        editDateText.isLongClickable = true

        // EditTextView is enabled for the StoreName field
        storeNameEdit.isClickable = true
        storeNameEdit.isLongClickable = true
        storeNameEdit.isFocusable = true
        storeNameEdit.isFocusableInTouchMode = true

        receiptInformationButton.isClickable = true

        // When the bottomsheet is collapsed the keyboard is hidden for any of the input fields.
        expenseForm.keys.forEach {inputField ->
            listener.hideKeyboard(inputField.first)
        }

        receiptVm.clearNewExpenseForm()
    }



    // Method for reactivating the input forms once the bottom sheet is collapsed.
    fun setPatronBottomSheetUnfocused() {
        // EditTextView is enabled for MaterialDatePicker
        editDateText.isClickable = true
        editDateText.isLongClickable = true

        // EditTextView is enabled for the StoreName field
        storeNameEdit.isClickable = true
        storeNameEdit.isLongClickable = true
        storeNameEdit.isFocusable = true
        storeNameEdit.isFocusableInTouchMode = true

        receiptInformationButton.isClickable = true

        // When the bottomsheet is collapsed the keyboard is hidden for any of the input fields.
        patronForm.keys.forEach {inputField ->
            listener.hideKeyboard(inputField.first)
        }

        receiptVm.clearNewPatronForm()
    }



    fun setMenuFocus() {
        // EditTextView for MaterialDatePicker is disabled
        editDateText.isClickable = false
        editDateText.isLongClickable = false

        // EditTextView for StoreName is disabled
        storeNameEdit.isClickable = false
        storeNameEdit.isLongClickable = false
        storeNameEdit.isFocusable = false

        receiptInformationButton.isClickable = false

        // When the bottomsheet is expanded the keyboard is hidden from the storename input field.
        listener.hideKeyboard(storeNameEdit)
    }



    fun setMenuUnfocused() {
        // EditTextView is enabled for MaterialDatePicker
        editDateText.isClickable = true
        editDateText.isLongClickable = true

        // EditTextView is enabled for the StoreName field
        storeNameEdit.isClickable = true
        storeNameEdit.isLongClickable = true
        storeNameEdit.isFocusable = true
        storeNameEdit.isFocusableInTouchMode = true

        receiptInformationButton.isClickable = true
    }



    // Method for deactivating the input forms once the bottom sheet is expanded
    fun setExpenseBottomSheetFocus() {
        // EditTextView for MaterialDatePicker is disabled
        editDateText.isClickable = false
        editDateText.isLongClickable = false

        // EditTextView for StoreName is disabled
        storeNameEdit.isClickable = false
        storeNameEdit.isLongClickable = false
        storeNameEdit.isFocusable = false

        receiptInformationButton.isClickable = false

        listener.onBottomSheetExpanded()

        // When the bottomsheet is expanded the keyboard is hidden from the storename input field.
        listener.hideKeyboard(storeNameEdit)

        receiptVm.clearNewExpenseForm()
    }



    fun setPatronBottomSheetFocus() {
        // EditTextView for MaterialDatePicker is disabled
        editDateText.isClickable = false
        editDateText.isLongClickable = false

        // EditTextView for StoreName is disabled
        storeNameEdit.isClickable = false
        storeNameEdit.isLongClickable = false
        storeNameEdit.isFocusable = false

        receiptInformationButton.isClickable = false

        listener.onBottomSheetExpanded()

        // When the bottomsheet is expanded the keyboard is hidden from the storename input field.
        listener.hideKeyboard(storeNameEdit)

        receiptVm.clearNewPatronForm()
    }



    fun showUpdateExpenseDialog(
        expenseId: Long,
        currentDescription: String,
        currentCost: String,
        currentQuantity: String
    ) {
        fragManager

        val newFragment = UpdateExpenseDialogFragment()

        newFragment.show(fragManager,"Update Expense Dialog")

        receiptVm._updateExpenseId.value = expenseId

        receiptVm._currentDescription.value = currentDescription

        receiptVm._currentCostText.value = currentCost

        receiptVm._currentQuantityText.value = currentQuantity

    }



    fun showUpdatePatronDialog(
        patronId: Long,
        currentName: String,
        currentPatron: PatronDataClass
    ) {

        fragManager

        val newFragment = UpdatePatronDialogFragment()

        newFragment.show(fragManager, "Update Patron Dialog")

        receiptVm._updatePatronID.value = patronId

        receiptVm._updatePatronName.value = currentName

        receiptVm._currentPatron.value = currentPatron
    }



    // Small values to be maintained within onSaveInstanceState()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("onSaveInstanceState() has been called")

        // Saving any Date Selected value from User Input
        outState.putString(KEY_DATE, (editDateText.text).toString())

        // Saving any Store Name value from User Input
        outState.putString(KEY_STORE, (storeNameEdit.text).toString())

    }
}

