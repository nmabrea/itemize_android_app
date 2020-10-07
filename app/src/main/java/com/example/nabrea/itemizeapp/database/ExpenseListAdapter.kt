package com.example.nabrea.itemizeapp.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.ExpandingFabAnimationInterface
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragment
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber

class ExpenseListAdapter internal constructor(
    override val animationContext: Context,
    private var patronAdapter: PatronListAdapter,
    var hostFragment: ReceiptFragment
) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>(),
    ExpandingFabAnimationInterface {

    private val inflater: LayoutInflater = LayoutInflater.from(animationContext)

    var expenseBasket = emptyList<ExpenseDataClass>()

    private val viewPool = RecyclerView.RecycledViewPool()

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expenseDescription: TextView = itemView.findViewById(R.id.expenseDescriptionCardText)
        val expenseCost: TextView = itemView.findViewById(R.id.expenseCostCardText)
        val expenseQuantity: TextView = itemView.findViewById(R.id.expenseQuantityCardText)
        val expenseSubTotal: TextView = itemView.findViewById(R.id.expenseSubTotalCardText)
        val availablePatrons: RecyclerView = itemView.findViewById(R.id.patronExpenseRecyclerView)
        val updateExpenseButton: ImageView = itemView.findViewById(R.id.updateExpenseButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_expense, parent, false)
        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val current = expenseBasket[position]

        var isMinimized = true

        holder.expenseDescription.text = current.description
        holder.expenseCost.text = holder.expenseCost.context.getString(R.string.itm_expenseCard_cost, current.costFormat)
        holder.expenseQuantity.text = holder.expenseQuantity.context.getString(R.string.itm_expenseCard_quantity, current.quantity)
        holder.expenseSubTotal.text = holder.expenseSubTotal.context.getString(R.string.itm_expenseCard_subCost, current.subCostFormat)
        holder.availablePatrons.apply {
            adapter = patronAdapter
            setRecycledViewPool(viewPool)
        }

        holder.expenseCost.visibility = MaterialTextView.GONE
        holder.expenseQuantity.visibility = MaterialTextView.GONE

        holder.itemView.setOnClickListener { view ->
            when (isMinimized) {
                true -> {
                    holder.expenseCost.startAnimation(fadeIn)
                    holder.expenseCost.visibility = MaterialTextView.VISIBLE

                    holder.expenseQuantity.startAnimation(fadeIn)
                    holder.expenseQuantity.visibility = MaterialTextView.VISIBLE

                    isMinimized = false
                }
                false -> {

                    holder.expenseCost.startAnimation(fadeOut)
                    holder.expenseCost.visibility = MaterialTextView.GONE

                    holder.expenseQuantity.startAnimation(fadeOut)
                    holder.expenseQuantity.visibility = MaterialTextView.GONE

                    isMinimized = true
                }
            }
        }

        holder.updateExpenseButton.setOnClickListener { button ->

            // TODO(02) Refactor to update current data with the updated data
            hostFragment.showUpdateExpenseDialog(current.expenseId!!)

            Timber.i("Updating: ${current.description}, ID: ${current.expenseId}")

        }

    }

    internal fun setExpenses(expenseBasket: List<ExpenseDataClass>) {
        this.expenseBasket = expenseBasket
        notifyDataSetChanged()
    }

    override fun getItemCount() = expenseBasket.size

}

class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseDataClass>() {
    override fun areItemsTheSame(oldItem: ExpenseDataClass, newItem: ExpenseDataClass): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(oldItem: ExpenseDataClass, newItem: ExpenseDataClass): Boolean {
        TODO("Not yet implemented")
    }
}