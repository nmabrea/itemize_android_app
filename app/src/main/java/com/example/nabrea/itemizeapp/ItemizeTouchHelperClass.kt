package com.example.nabrea.itemizeapp

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.activity.ItemizeViewModel
import com.example.nabrea.itemizeapp.database.ExpenseListAdapter

class ItemizeTouchHelperClass(private val viewModel: ItemizeViewModel,
                              private val adapter: ExpenseListAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        viewHolder as ExpenseListAdapter.ExpenseViewHolder

        when (direction) {

            ItemTouchHelper.RIGHT -> {



                viewModel.deleteExpense(adapter.expenseBasket[viewHolder.adapterPosition])

                val swipeMessage = "${(adapter.expenseBasket[viewHolder.adapterPosition]).description} has been removed from the list."

                viewModel._message.value = EventClass(swipeMessage)

            }
        }
    }


}