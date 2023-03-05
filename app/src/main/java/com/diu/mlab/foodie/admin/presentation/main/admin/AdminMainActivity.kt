package com.diu.mlab.foodie.admin.presentation.main.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.diu.mlab.foodie.admin.databinding.ActivityAdminMainBinding
import com.diu.mlab.foodie.admin.databinding.ActivityLoginBinding
import com.diu.mlab.foodie.admin.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {
    private val viewModel : AdminMainViewModel by viewModels()
    private lateinit var binding: ActivityAdminMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}