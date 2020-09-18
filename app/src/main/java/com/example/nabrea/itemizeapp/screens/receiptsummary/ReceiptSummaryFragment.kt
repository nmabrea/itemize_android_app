package com.example.nabrea.itemizeapp.screens.receiptsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.databinding.FragmentReceiptSummaryBinding

class ReceiptSummaryFragment : Fragment() {

    lateinit var receiptSummaryBinding: FragmentReceiptSummaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        receiptSummaryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_summary, container, false)

        return receiptSummaryBinding.root
    }
}