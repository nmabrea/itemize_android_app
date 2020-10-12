package com.example.nabrea.itemizeapp.screens.finalize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.databinding.FragmentFinalizeBinding

class FinalizeFragment : Fragment() {

    lateinit var finalizeBinding: FragmentFinalizeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        finalizeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_finalize, container, false)

        return finalizeBinding.root
    }
}