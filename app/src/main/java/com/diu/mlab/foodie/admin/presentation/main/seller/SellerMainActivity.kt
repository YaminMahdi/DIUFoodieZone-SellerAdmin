package com.diu.mlab.foodie.admin.presentation.main.seller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.ActivitySellerMainBinding
import com.diu.mlab.foodie.admin.util.changeStatusBarColor
import com.diu.mlab.foodie.admin.util.getTopic
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellerMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Declare the launcher at the top of your Activity/Fragment:
        Firebase.messaging.subscribeToTopic(Firebase.auth.currentUser?.email?.getTopic() ?: "null")

        val manager: FragmentManager = supportFragmentManager

        binding.bubbleTabBar.addBubbleListener { id ->
            when(id){
                R.id.orderList -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.tia))
                    this.changeStatusBarColor(R.color.tiaX,false)
                    manager.beginTransaction()
                        .replace(binding.sellFragment.id, OrderListFragment())
                        .commit()
                }
                R.id.foodList -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.blueX))
                    this.changeStatusBarColor(R.color.blueZ,false)
                    manager.beginTransaction()
                        .replace(binding.sellFragment.id, FoodListFragment())
                        .commit()
                }
                R.id.addFood -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.greenPop))
                    this.changeStatusBarColor(R.color.greenZ,false)
                    manager.beginTransaction()
                        .replace(binding.sellFragment.id, FoodAddFragment())
                        .commit()
                }
                R.id.profile -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.tia))
                    this.changeStatusBarColor(R.color.tiaX,false)
                    manager.beginTransaction()
                        .replace(binding.sellFragment.id, SellerProfileFragment())
                        .commit()
                }
            }
        }
    }
}