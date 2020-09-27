package com.example.nabrea.itemizeapp.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.screens.receipt.expense.ExpenseDataClass

class ExpenseListAdapter internal constructor(
    context: Context,
    private var patronAdapter: PatronListAdapter
) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var expenseBasket = emptyList<ExpenseDataClass>()
    private val viewPool = RecyclerView.RecycledViewPool()

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expenseDescription: TextView = itemView.findViewById(R.id.expenseDescriptionCardText)
        val expenseCost: TextView = itemView.findViewById(R.id.expenseCostCardText)
        val expenseQuantity: TextView = itemView.findViewById(R.id.expenseQuantityCardText)
        val expenseSubTotal: TextView = itemView.findViewById(R.id.expenseSubTotalCardText)
        val availablePatrons: RecyclerView = itemView.findViewById(R.id.patronExpenseRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_expense, parent, false)
        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val current = expenseBasket[position]
        holder.expenseDescription.text = current.description
        holder.expenseCost.text = holder.expenseCost.context.getString(R.string.itm_expenseCard_cost, current.costFormat)
        holder.expenseQuantity.text = holder.expenseQuantity.context.getString(R.string.itm_expenseCard_quantity, current.quantity)
        holder.expenseSubTotal.text = holder.expenseSubTotal.context.getString(R.string.itm_expenseCard_subCost, current.subCostFormat)

        holder.availablePatrons.apply {
            adapter = patronAdapter
            setRecycledViewPool(viewPool)
        }
    }

    internal fun setExpenses(expenseBasket: List<ExpenseDataClass>) {
        this.expenseBasket = expenseBasket
        notifyDataSetChanged()
    }

    override fun getItemCount() = expenseBasket.size

}