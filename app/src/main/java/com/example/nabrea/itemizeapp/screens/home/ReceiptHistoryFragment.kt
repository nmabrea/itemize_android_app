package com.example.nabrea.itemizeapp.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.databinding.FragmentReceiptHistoryBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ReceiptHistoryFragment : Fragment() {

    private lateinit var receiptHistoryBinding: FragmentReceiptHistoryBinding
    private lateinit var mainBab: BottomAppBar
    private lateinit var navController: NavController
    private lateinit var addButton: FloatingActionButton
    private lateinit var fragDirections: NavDirections


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Below sets up the Receipt History Fragment for drawer menu navigation
        setHasOptionsMenu(true)

        // Layout for this fragment is inflated using Data Binding
        receiptHistoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_history, container, false)

        mainBab = this.activity!!.findViewById(R.id.bottom_app_bar)


        addButton = this.activity!!.findViewById(R.id.button_primary_action)

        fragDirections = ReceiptHistoryFragmentDirections.actionReceiptHistoryFragmentToReceiptFragment()

        navController = this.findNavController()

        return receiptHistoryBinding.root
    }

    override fun onResume() {
        super.onResume()
    }

}