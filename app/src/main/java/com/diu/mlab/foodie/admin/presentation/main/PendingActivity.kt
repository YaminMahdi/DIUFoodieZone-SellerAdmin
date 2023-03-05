package com.diu.mlab.foodie.admin.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diu.mlab.foodie.admin.databinding.ActivityPendingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}