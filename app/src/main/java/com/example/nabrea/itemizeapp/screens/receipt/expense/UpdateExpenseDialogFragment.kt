package com.example.nabrea.itemizeapp.screens.receipt.expense

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.activity.ItemizeViewModel
import com.example.nabrea.itemizeapp.databinding.FragmentDialogUpdateExpenseBinding
import com.google.android.material.textfield.TextInputEditText

class UpdateExpenseDialogFragment : DialogFragment() {

    private lateinit var updateExpenseBinding: FragmentDialogUpdateExpenseBinding

    private lateinit var updateDescriptionEdit: TextInputEditText

    private lateinit var updateCostEdit: TextInputEditText

    private lateinit var updateQuantityEdit: TextInputEditText

    private lateinit var updateExpenseVm: ItemizeViewModel

    private lateinit var updateDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        updateExpenseBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dialog_update_expense,
            container,
            false
        )

        //TODO(02) Set textwatcher on costedit, set validation inputs, if null use old values.
        updateDescriptionEdit = updateExpenseBinding.updateExpenseDescriptionEdit

        updateCostEdit = updateExpenseBinding.updateExpenseCostEdit

        updateQuantityEdit = updateExpenseBinding.updateExpenseQuantityEdit

        updateExpenseVm = ViewModelProvider(activity!!).get(ItemizeViewModel::class.java)

        updateExpenseBinding.itemizeViewModel = updateExpenseVm

        return updateExpenseBinding.root
    }

    override fun onStart() {
        super.onStart()

        updateDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

    }

    override fun onResume() {
        super.onResume()

        updateExpenseVm._currentDescription.observe(this, { currentDescription ->

            updateExpenseVm._updateDescription.value = currentDescription

        })

        updateExpenseVm._currentCostText.observe(this, { currentCost ->

            updateExpenseVm._updateCostText.value = currentCost

        })

        updateExpenseVm._currentQuantityText.observe(this, { currentQuantity ->

            updateExpenseVm._updateQuantityText.value = currentQuantity

        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        updateDialog = super.onCreateDialog(savedInstanceState)

        updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        updateDialog.window?.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.itm_dialog_inset, null))

        return updateDialog
    }
}