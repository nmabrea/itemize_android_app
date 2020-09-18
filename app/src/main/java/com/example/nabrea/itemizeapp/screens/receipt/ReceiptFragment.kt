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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.nabrea.itemizeapp.ExpandingFabAnimationInterface
import com.example.nabrea.itemizeapp.ItemizeTextWatcherClass
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.databinding.FragmentReceiptBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber

// Small constant values to be saved within onSavedInstanceState() below.
// Constant value for the Date Selected via MaterialDatePicker in the Receipt Fragment
const val KEY_DATE: String = "key_date"

// Constant value for the UserInput StoreName via EditTextField in the Receipt Fragment
const val KEY_STORE: String = "key_store"

enum class ErrorMessages (val errorMessage: String) {

    KEY_ERROR_GENERAL("Please fill in all fields correctly."),
    KEY_ERROR_DESCRIPTION("Enter a valid description. (20 characters max)"),
    KEY_ERROR_COST("Enter a valid cost. (8 digits max)"),
    KEY_ERROR_QUANTITY("Enter a valid quantity. (3 digits max)"),
    KEY_ERROR_NAME("Enter a valid name. (20 characters max)")

}


// Interface to be used a communication between an instance of this Fragment and the MainActivity
interface ReceiptFragmentCommunication {

    // Function to be called when a BottomSheet is STATE_COLLAPSED
    fun onBottomSheetCollapsed()

    // Function to be called when a BottomSheet is STATE_EXPANDED
    fun onBottomSheetExpanded()

    // Function to be called when Activity needs to display ReceiptFragment's menu actions
    fun setMenuAwareness(menu: MenuClass)

    fun hideKeyboard(view: View)

    fun displaySnackbar(message: String)

}

class ReceiptFragment : Fragment(),
    ExpandingFabAnimationInterface,
    MaterialDatePickerInterface {

    // Variable to establish the interface listener
    lateinit var listener: ReceiptFragmentCommunication

    // DataBinding variable for this Fragment
    private lateinit var receiptBinding: FragmentReceiptBinding

    // ViewModel associated with this Fragment
    private lateinit var receiptVm: ReceiptViewModel

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

    private lateinit var expenseDescription: TextInputLayout

    private lateinit var expenseDescriptionEditText: TextInputEditText

    private lateinit var expenseCost: TextInputLayout

    private lateinit var expenseCostEditText: TextInputEditText

    private lateinit var expenseQuantity: TextInputLayout

    private lateinit var expenseQuantityEditText: TextInputEditText

    private lateinit var expenseForm: MutableMap<Pair<TextInputEditText, TextInputLayout>, String>

    // Variable for the AddPatron Button
    private lateinit var patronButton: FloatingActionButton

    // Variable for the AddPatron Button's associated label
    private lateinit var patronLabel: MaterialTextView

    // Variable for the Layout view associated with the AddPatron Button
    private lateinit var patronLayout: ConstraintLayout

    // BottomSheetClass variable that passes in the Layout associated with the AddPatron Button
    private lateinit var patronBottomSheet: BottomSheetClass

    private lateinit var patronFirstName: TextInputLayout

    private lateinit var patronLastName: TextInputLayout

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

    // Instance of expandable menu items that appear with Navigation Actions
    private lateinit var menuNavActions: MutableMap<
            Pair<FloatingActionButton, MaterialTextView>, NavDirections>



    // Override values inherited from the MaterialDatePickerInterface below
    // Variable for building the DatePicker display
    override lateinit var builder: MaterialDatePicker.Builder<*>

    // Picker instance variable
    override lateinit var picker: MaterialDatePicker<*>

    private lateinit var editDate: TextInputLayout

    // The EditTextField associated with the DatePicker instance within the receipt_fragment.xml
    override lateinit var editDateText: TextInputEditText

    private lateinit var storeName: TextInputLayout

    // The TextInputEditText field associated with the Store Name input.
    private lateinit var storeNameEdit: TextInputEditText

    // Variable identifying the FragmentManager
    override lateinit var fragManager: FragmentManager

    // Override values inherited from the ExpandingFabAnimation interface
    override lateinit var animationContext: Context



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
            false)

        // Getting the ReceiptViewModel
        receiptVm = ViewModelProvider(this).get(ReceiptViewModel::class.java)

        // Associating the layout xml with the ViewModel for direct communication
        receiptBinding.receiptViewModel = receiptVm

        // Identifying this Fragment as the lifecycleOwner of the ViewModel
        receiptBinding.lifecycleOwner = this

        // Navigation Options setup within the action bar for this Fragment
        setHasOptionsMenu(true)

        // NAVIGATION UI::Locating the NavController for Navigation
        navController = this.findNavController()

        // Locating the text view storing the StoreName within the onSavedInstanceState()
        storeNameEdit = receiptBinding.receiptStoreNameEdit




        // Below is the code sequence for the expandable FAB menu:
        // Setting the context for the FAB menu animations
        animationContext = receiptBinding.root.context

        // Creating the expandMenu object through the custom ActionButton() class
        expandMenu = MenuClass(navController, animationContext)

        // Identifying the transparent background to supplement BottomSheetBehavior actions
        stateExpandedBackground = receiptBinding.bottomSheetBackground.bottomSheetBackground

        // Locating the expense button, and its associated label and bottom_sheet_expense.xml
        expenseButton = receiptBinding.buttonAddExpense

        // Locating the label text associated with the expense button
        expenseLabel = receiptBinding.labelAddExpense

        // Locating the expenseLayout, which is included as part of the receiptBinding
        expenseLayout = receiptBinding.expenseBottomSheet.expenseBottomSheetLayout

        // Creating an instance of the BottomSheetClass for the Expense Bottom Sheet
        expenseBottomSheet = BottomSheetClass(
            expenseLayout,
            stateExpandedBackground,
            listener,
            this,
            animationContext)

        receiptVm._expenseBottomSheet.value = expenseBottomSheet

        expenseDescription = receiptBinding.expenseBottomSheet.expenseDescription

        expenseDescriptionEditText = receiptBinding.expenseBottomSheet.expenseDescriptionEdit

        expenseDescriptionEditText.filters = arrayOf(InputFilter { charSequence, i, i2, spanned, i3, i4 ->
            return@InputFilter charSequence.replace(Regex("[^a-zA-Z ]*"), "")
        })

        expenseCost = receiptBinding.expenseBottomSheet.expenseCost

        expenseCostEditText = receiptBinding.expenseBottomSheet.expenseCostEdit

        expenseQuantity = receiptBinding.expenseBottomSheet.expenseQuantity

        expenseQuantityEditText = receiptBinding.expenseBottomSheet.expenseQuantityEdit

        expenseForm = mutableMapOf()

        expenseForm[Pair(expenseDescriptionEditText, expenseDescription)] =
            ErrorMessages.KEY_ERROR_DESCRIPTION.errorMessage

        expenseForm[Pair(expenseCostEditText, expenseCost)] =
            ErrorMessages.KEY_ERROR_COST.errorMessage

        expenseForm[Pair(expenseQuantityEditText, expenseQuantity)] =
            ErrorMessages.KEY_ERROR_QUANTITY.errorMessage


        // Locating the patron button, and its associated label and bottom_sheet_patron.xml
        patronButton = receiptBinding.buttonAddPatron

        // Locating the label text associated with the patron button
        patronLabel = receiptBinding.labelAddPatron

        // Locating the patronLayout, which is included as part of receiptBinding
        patronLayout = receiptBinding.patronBottomSheet.patronBottomSheetLayout

        // Creating an instance of the BottomSheetClass for the Patron Bottom Sheet
        patronBottomSheet = BottomSheetClass(
            patronLayout,
            stateExpandedBackground,
            listener,
            this,
            animationContext)

        receiptVm._patronBottomSheet.value = patronBottomSheet



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
        listener.setMenuAwareness(expandMenu)

        return receiptBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("$listener is activated")

    }




    override fun onResume() {
        super.onResume()
        Timber.i("onResume() has been called")

        receiptVm.message.observe(this, Observer { message ->
            message.getContentIfNotHandled()?.let { content ->
                listener.displaySnackbar(content)
            }
        })

        receiptVm.expenseBottomSheet.observe(this, Observer { eBottomSheet ->
            when (eBottomSheet.bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
               true -> {
                   expenseBottomSheet.bottomSheetBehavior.state =
                       BottomSheetBehavior.STATE_COLLAPSED
               }
            }
        })

        receiptVm.patronBottomSheet.observe(this, Observer { pBottomSheet ->
            when (pBottomSheet.bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                true -> {
                    patronBottomSheet.bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })

        ItemizeTextWatcherClass().setCurrencyTextWatcher(expenseCostEditText)

        receiptVm.error.observe(this, Observer { errorMessage ->

            if (errorMessage == null) {
                expenseForm.forEach {inputField ->
                    inputField.key.second.error = null
                }
            } else {

                expenseForm.forEach { inputField ->

                    if (!isInputValid(inputField.key.first)) {
                        inputField.key.second.error = inputField.value
                    } else {
                        inputField.key.second.error = null
                    }
                }
            }
        })

        // TODO (02) Figure out if the MaterialDatePicker code can be in its own class

        /* Code for initiating the Material Date Picker from MaterialDatePicker interface below:
         * Initializes the Date Picker from Material library */
        builder = MaterialDatePicker.Builder.datePicker()

        // setTitle() sets the initial placeholder title of the Date Picker
        setTitle()

        // Instantializes the Date Picker from the library within this Fragment
        picker = builder.build()

        // Identifies the receiptDateEdit View to be edited based on date selection.
        editDateText = receiptBinding.receiptDateEdit

        // Identifies this Fragment's fragmentManager
        fragManager = fragmentManager!!

        // Initiates listeners for click actions that occur during Date Picker
        clickActions(listener)


    }




    private fun isInputValid(userInput: TextInputEditText) : Boolean {
        return if (userInput.text == null) {
            false
        } else if (userInput.text!!.isEmpty()) {
            false
        } else !userInput.text!!.contains("0.00")
    }




    fun setBottomSheetUnfocused() {
        // EditTextView is enabled for MaterialDatePicker
        editDateText.isClickable = true
        editDateText.isLongClickable = true

        // EditTextView is enabled for the StoreName field
        storeNameEdit.isClickable = true
        storeNameEdit.isLongClickable = true
        storeNameEdit.isFocusable = true
    }

    fun setBottomSheetFocus() {
        // EditTextView for MaterialDatePicker is disabled
        editDateText.isClickable = false
        editDateText.isLongClickable = false

        // EditTextView for StoreName is disabled
        storeNameEdit.isClickable = false
        storeNameEdit.isLongClickable = false

        listener.hideKeyboard(storeNameEdit)
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

