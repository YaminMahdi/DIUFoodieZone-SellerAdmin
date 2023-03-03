package com.diu.mlab.foodie.admin.presentation.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diu.mlab.foodie.admin.R

class SellerRegFragment : Fragment() {

    companion object {
        fun newInstance() = SellerRegFragment()
    }

    private lateinit var viewModel: SellerRegViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seller_reg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[SellerRegViewModel::class.java]
        // TODO: Use the ViewModel
    }

}