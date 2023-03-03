package com.diu.mlab.foodie.admin.presentation.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.diu.mlab.foodie.admin.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}