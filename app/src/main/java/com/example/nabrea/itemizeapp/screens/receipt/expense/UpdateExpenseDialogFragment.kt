package com.example.nabrea.itemizeapp.screens.receipt.expense

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.nabrea.itemizeapp.ItemizeTextWatcherClass
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.activity.ItemizeViewModel
import com.example.nabrea.itemizeapp.databinding.FragmentDialogUpdateExpenseBinding
import com.example.nabrea.itemizeapp.screens.receipt.ErrorMessages
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragmentCommunication
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber

class UpdateExpenseDialogFragment : DialogFragment() {

    private lateinit var updateExpenseBinding: FragmentDialogUpdateExpenseBinding

    private lateinit var updateDescription: TextInputLayout

    private lateinit var updateDescriptionEdit: TextInputEditText

    private lateinit var updateCost: TextInputLayout

    private lateinit var updateCostEdit: TextInputEditText

    private lateinit var updateQuantity: TextInputLayout

    private lateinit var updateQuantityEdit: TextInputEditText

    private lateinit var updateExpenseForm: MutableMap<Pair<TextInputEditText, TextInputLayout>, String>

    private lateinit var updateExpenseVm: ItemizeViewModel

    private lateinit var updateDialog: Dialog

    private lateinit var cancelDialogButton: MaterialTextView

    private lateinit var updateDialogButton: MaterialButton

    private lateinit var listener: ReceiptFragmentCommunication



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.i("OnCreateDialog() is called")
        updateDialog = super.onCreateDialog(savedInstanceState)

        updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        updateDialog.window?.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.itm_dialog_inset, null))

        return updateDialog
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as ReceiptFragmentCommunication
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Timber.i("OnCreateView() is called")

        updateExpenseBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog_update_expense,
            container,
            false
        )

        updateDescription = updateExpenseBinding.updateExpenseDescription

        updateDescriptionEdit = updateExpenseBinding.updateExpenseDescriptionEdit

        updateCost = updateExpenseBinding.updateExpenseCost

        updateCostEdit = updateExpenseBinding.updateExpenseCostEdit

        updateQuantity = updateExpenseBinding.updateExpenseQuantity

        updateQuantityEdit = updateExpenseBinding.updateExpenseQuantityEdit





        updateExpenseForm = mutableMapOf()

        updateExpenseForm[Pair(updateDescriptionEdit, updateDescription)] =
            ErrorMessages.KEY_ERROR_DESCRIPTION.errorMessage

        updateExpenseForm[Pair(updateCostEdit, updateCost)] =
            ErrorMessages.KEY_ERROR_COST.errorMessage

        updateExpenseForm[Pair(updateQuantityEdit, updateQuantity)] =
            ErrorMessages.KEY_ERROR_QUANTITY.errorMessage



        updateExpenseVm = ViewModelProvider(activity!!).get(ItemizeViewModel::class.java)

        updateExpenseBinding.itemizeViewModel = updateExpenseVm

        cancelDialogButton = updateExpenseBinding.dialogButtonCancel

        updateDialogButton = updateExpenseBinding.dialogButtonUpdate

        return updateExpenseBinding.root
    }



    override fun onStart() {
        super.onStart()
        Timber.i("OnStart() is called")

        updateDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        updateDialog.setCanceledOnTouchOutside(false)

    }



    override fun onResume() {
        super.onResume()
        Timber.i("OnResume() is called")

        cancelDialogButton.setOnClickListener { closeDialog() }

        updateDescriptionEdit.filters = arrayOf(InputFilter { charSequence, i, i2, spanned, i3, i4 ->
            return@InputFilter charSequence.replace(Regex("[^a-zA-Z0-9 ]*"), "")
        })

        ItemizeTextWatcherClass().setStringTextWatcher(updateDescriptionEdit)

        updateExpenseVm._currentDescription.observe(this, { currentDescription ->

            updateExpenseVm._updateDescription.value = currentDescription

        })



        ItemizeTextWatcherClass().setCurrencyTextWatcher(updateCostEdit)

        updateExpenseVm._currentCostText.observe(this, { currentCost ->

            updateExpenseVm._updateCostText.value = currentCost

        })



        ItemizeTextWatcherClass().setCurrencyTextWatcher(updateQuantityEdit)

        updateExpenseVm._currentQuantityText.observe(this, { currentQuantity ->

            updateExpenseVm._updateQuantityText.value = currentQuantity

        })



        // Setting an observer for ViewModel variables that inform the program of errors with the expenseForm
        updateExpenseVm.errorExpense.observe(this, { errorMessage ->

            // If the ViewModel variable is null, all of the associated error values for the views are null
            if (errorMessage == null) {
                updateExpenseForm.forEach { inputField ->
                    inputField.key.second.error = null
                }
            } else {

                // If the ViewModel variable has an error message, all input fields are checked
                // and error messages will be displayed based on the validation method's criteria
                updateExpenseForm.forEach { inputField ->

                    if (!isExpenseInputValid(inputField.key.first)) {
                        inputField.key.second.error = inputField.value
                    } else {
                        inputField.key.second.error = null
                    }
                }
            }
        })



        updateExpenseVm.isUpdated.observe(this, { expenseUpdated ->

            when (expenseUpdated) {
                true -> closeDialog()
            }

        })
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



    private fun closeDialog() {
        onStop()
        onDestroyView()
        onDestroy()
        onDetach()
    }



    override fun onStop() {
        super.onStop()
        Timber.i("OnStop() is called")

        updateExpenseVm.clearUpdateExpenseForm()

        updateExpenseForm.keys.forEach { viewPair ->

            listener.hideKeyboard(viewPair.first)

            viewPair.first.isFocusable = false

            viewPair.first.isFocusableInTouchMode = false

            viewPair.first.isTextInputLayoutFocusedRectEnabled = false

        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("OnDestroyView() is called")
    }


    override fun onDestroy() {
        super.onDestroy()
        Timber.i("OnDestroy() is called")
    }


    override fun onDetach() {
        super.onDetach()
        Timber.i("OnDetach() is called")
    }
}