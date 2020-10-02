package com.example.nabrea.itemizeapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.activity.ItemizeViewModel
import com.example.nabrea.itemizeapp.database.ExpenseListAdapter

class ItemizeTouchHelperClass(private val viewModel: ItemizeViewModel,
                              private val adapter: ExpenseListAdapter,
                              private val context: Context
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

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        viewHolder as ExpenseListAdapter.ExpenseViewHolder

        val cardView = viewHolder.itemView

        val background = ColorDrawable()

        background.color = ContextCompat.getColor(context, R.color.itm_secondary_orangeDark)

        background.bounds = Rect(
            cardView.right - dX.toInt() * 15,
            cardView.top + 26,
            cardView.right - dX.toInt() / 2,
            cardView.bottom
        )

        background.draw(canvas)

        val iconDelete =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)!!.toBitmap()

        val iconDestination =
            RectF(
                iconDelete.width.toFloat(),
                (cardView.top + ((cardView.height - iconDelete.height) / 2)).toFloat(),
                (iconDelete.width * 2).toFloat(),
                (cardView.top + ((cardView.height - iconDelete.height) / 2)) + iconDelete.height.toFloat()
            )

        canvas.drawBitmap(iconDelete, null, iconDestination, Paint())

        super
            .onChildDraw(
                canvas,
                recyclerView,
                viewHolder,
                dX / 4,
                dY,
                actionState,
                isCurrentlyActive
            )
    }
}