package com.diu.mlab.foodie.admin.presentation.main.seller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.diu.mlab.foodie.admin.databinding.ActivityAdminMainBinding
import com.diu.mlab.foodie.admin.databinding.ActivitySellerMainBinding
import com.diu.mlab.foodie.admin.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerMainActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivitySellerMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}