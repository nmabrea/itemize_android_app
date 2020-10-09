package com.example.nabrea.itemizeapp.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragment
import com.example.nabrea.itemizeapp.screens.receipt.patron.PatronDataClass
import timber.log.Timber

class PatronListAdapter internal constructor(
    context: Context,
    private var hostFragment: Fragment
) : RecyclerView.Adapter<PatronListAdapter.PatronViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var patronGroup = emptyList<PatronDataClass>()

    class PatronViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patronInitials: TextView = itemView.findViewById(R.id.patronInitialsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatronViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_patron, parent, false)
        return PatronViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatronViewHolder, position: Int) {
        val current = patronGroup[position]
        holder.patronInitials.text = current.nameInitials

        holder.patronInitials.setOnClickListener { view ->

            (hostFragment as ReceiptFragment)
                .showUpdatePatronDialog(
                    current.patronID!!,
                    current.name,
                    current
                )

            Timber.i("Updating: ${current.name}, ID: ${current.patronID}")

        }



    }

    internal fun setPatrons(patronGroup: List<PatronDataClass>) {
        this.patronGroup = patronGroup
        notifyDataSetChanged()
    }

    override fun getItemCount() = patronGroup.size

}