package com.diu.mlab.foodie.admin.presentation.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.diu.mlab.foodie.admin.databinding.ActivityRegistrationBinding
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.jem.fliptabs.FlipTab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setBounceClickListener()
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val manager: FragmentManager = supportFragmentManager

        binding.flipTab.setTabSelectedListener(object: FlipTab.TabSelectedListener {
            override fun onTabSelected(isLeftTab: Boolean, tabTextValue: String) {
                if (isLeftTab){
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, SellerRegFragment()).commit()
                }else{
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, AdminRegFragment()).commit()
                }
            }
            override fun onTabReselected(isLeftTab: Boolean, tabTextValue: String) {}
        })
    }
}