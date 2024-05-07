package com.diu.mlab.foodie.admin.presentation.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.diu.mlab.foodie.admin.databinding.ActivityLoginBinding
import com.diu.mlab.foodie.admin.domain.RequestState
import com.diu.mlab.foodie.admin.presentation.main.admin.AdminMainActivity
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.presentation.main.seller.SellerMainActivity
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Log.d("TAG", "Notification PERMISSION_GRANTED")
        } else {
            Log.d("TAG", "Notification PERMISSION_DENIED")
            Toast.makeText(this, "Must give permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
            PackageManager.PERMISSION_GRANTED
        ) requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        askNotificationPermission()

//        ReturnTransitionPatcher.patchAll(application)
//        window.sharedElementEnterTransition = ChangeBounds().setDuration(1000000000000000000)
//        window.sharedElementExitTransition = ChangeBounds().setDuration(1000000000000000000)
//        val fade = Fade()
//        fade.duration = 200
//        fade.excludeTarget(android.R.id.statusBarBackground, true)
//        fade.excludeTarget(android.R.id.navigationBarBackground, true)
//
//        window.enterTransition = fade
//        window.exitTransition = fade
//        binding.signInBtn.setSize(SignInButton.SIZE_WIDE)
        binding.signInBtn.setBounceClickListener {
            viewModel.googleSignIn(this){result ->
                when(result){
                    is RequestState.Error -> {
                        Log.e("TAG", "failed: ${result.error}")
                        if(result.code!= 20)
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is RequestState.Success -> {
                        viewModel.firebaseLogin(result.data,
                            success = {
                                Log.d("TAG", "success:")
                                if(it.status=="accepted"){
                                    when(it.userType){
                                        "admin" -> startActivity(Intent(this,AdminMainActivity::class.java))
                                        else -> startActivity(Intent(this,SellerMainActivity::class.java))
                                    }
                                    finish()
                                }
                                else{
                                    val intent = Intent(this, PendingActivity::class.java)
                                    intent.putExtra("status", it.status)
                                    startActivity(intent)
                                    finish()
                                }

                            }, failed = {
                                Log.e("TAG", "failed: $it")
                                MainScope().launch {
                                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }
            }
        }
        binding.signUpBtn.setBounceClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }
        viewModel.loadingVisibility.observe(this){
            binding.loadingLayout.visibility = if(it) View.VISIBLE else View.GONE
        }
    }
}