package com.example.nabrea.itemizeapp.screens.receipt.patron

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nabrea.itemizeapp.EventClass
import com.example.nabrea.itemizeapp.ItemizeTextWatcherClass
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.activity.ItemizeViewModel
import com.example.nabrea.itemizeapp.databinding.FragmentDialogUpdatePatronBinding
import com.example.nabrea.itemizeapp.screens.receipt.ErrorMessages
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragmentCommunication
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import timber.log.Timber

class UpdatePatronDialogFragment : DialogFragment() {

    private lateinit var updatePatronBinding: FragmentDialogUpdatePatronBinding

    private lateinit var updatePatronName: TextInputLayout

    private lateinit var updatePatronNameEdit: TextInputEditText

    private lateinit var updatePatronForm: MutableMap<Pair<TextInputEditText, TextInputLayout>, String>

    private lateinit var updatePatronVm: ItemizeViewModel

    private lateinit var updateDialog: Dialog

    private lateinit var cancelDialogButton: MaterialTextView

    private lateinit var updateDialogButton: MaterialButton

    private lateinit var deleteDialogButton: MaterialTextView

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

        updatePatronBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog_update_patron,
            container,
            false
        )

        updatePatronName = updatePatronBinding.updatePatronName

        updatePatronNameEdit = updatePatronBinding.updatePatronNameEdit

        updatePatronForm = mutableMapOf()

        updatePatronForm[Pair(updatePatronNameEdit, updatePatronName)] =
            ErrorMessages.KEY_ERROR_NAME.errorMessage



        updatePatronVm = ViewModelProvider(activity!!).get(ItemizeViewModel::class.java)

        updatePatronBinding.itemizeViewModel = updatePatronVm

        cancelDialogButton = updatePatronBinding.dialogButtonCancel

        updateDialogButton = updatePatronBinding.dialogButtonUpdate

        deleteDialogButton = updatePatronBinding.dialogButtonDelete

        return updatePatronBinding.root
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

        updatePatronNameEdit.filters = arrayOf(InputFilter { charSequence, i, i2, spanned, i3, i4 ->
            return@InputFilter charSequence.replace(Regex("[^a-zA-Z ]*"), "")
        })

        ItemizeTextWatcherClass().setStringTextWatcher(updatePatronNameEdit)

        updatePatronVm._currentPatronName.observe(this, { currentName ->

            updatePatronVm._updatePatronName.value = currentName

        })

        updatePatronVm._currentPatron.observe(this, { currentPatron ->

            deleteDialogButton.setOnClickListener {

                updatePatronVm.viewModelScope.launch {

                    updatePatronVm.deleteSelectedPatron(currentPatron)

                }

                updatePatronVm._message.value = EventClass("${currentPatron.name} has been removed.")

                closeDialog()

            }
        })



        // Setting an observer for ViewModel variables that inform the program of errors with the expenseForm
        updatePatronVm.errorPatron.observe(this, { errorMessage ->

            // If the ViewModel variable is null, all of the associated error values for the views are null
            if (errorMessage == null) {
                updatePatronForm.forEach { inputField ->
                    inputField.key.second.error = null
                }
            } else {

                // If the ViewModel variable has an error message, all input fields are checked
                // and error messages will be displayed based on the validation method's criteria
                updatePatronForm.forEach { inputField ->

                    if (!isPatronInputValid(inputField.key.first)) {
                        inputField.key.second.error = inputField.value
                    } else {
                        inputField.key.second.error = null
                    }
                }
            }
        })



        updatePatronVm.isUpdated.observe(this, { patronUpdated ->

            when (patronUpdated) {
                true -> closeDialog()
            }

        })
    }



    // Method for validation criteria for various requirements of the expense form
    private fun isPatronInputValid(userInput: TextInputEditText) : Boolean {

        val userInputTrimmed = userInput.text!!.trim()

        return when {

            userInput.text.isNullOrBlank() -> { false }

            userInputTrimmed.split(" ").size <= 1 -> { false }

            else -> userInput.text!!.isNotEmpty()
        }
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

        updatePatronVm.clearUpdatePatronForm()

        updatePatronForm.keys.forEach { viewPair ->

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