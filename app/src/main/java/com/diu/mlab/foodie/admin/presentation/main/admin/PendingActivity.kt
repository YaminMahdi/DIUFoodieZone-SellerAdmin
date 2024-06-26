package com.diu.mlab.foodie.admin.presentation.main.admin

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.diu.mlab.foodie.admin.databinding.ActivityPendingBinding
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingBinding
    private val viewModel : AdminMainViewModel by viewModels()
    private lateinit var emailId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        emailId = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"
        Log.d("TAG", "onCreate: $emailId")
        viewModel.getMyProfile(emailId){
            Log.d("TAG", "onCreate getMyProfile: $it")
        }

        viewModel.getActiveAdminList{
            Log.d("TAG", "onCreate getActiveAdminList: $it")
        }

        viewModel.myProfile.observe(this){ user->
            binding.nm.text = user.nm
            user.pic.getDrawable{ binding.pic.setImageDrawable(it) }
            Log.d("TAG", "onCreate: myProfile observe $user")
        }

        viewModel.adminList.observe(this){
            val adapter = AdminListViewAdapter(it)
            binding.adminList.adapter = adapter
            Log.d("TAG", "onCreate: adminList observe ${it.count()}")
        }

    }
}