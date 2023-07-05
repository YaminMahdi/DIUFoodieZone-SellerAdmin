package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.ActivityLoginBinding
import com.diu.mlab.foodie.admin.presentation.main.admin.AdminMainActivity
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.presentation.main.seller.SellerMainActivity
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
            viewModel.firebaseLogin(credential,{
                preferencesEditor.putString("email", it.email).apply()

                Log.d("TAG", "success:")
                if(it.status=="accepted"){
                    when(it.userType){
                        "admin" -> startActivity(Intent(this,AdminMainActivity::class.java))
                        else -> startActivity(Intent(this,SellerMainActivity::class.java))
                    }
                }
                else{
                    val intent = Intent(this, PendingActivity::class.java)
                    intent.putExtra("status", it.status)
                    startActivity(intent)
                }

            }){
                Log.e("TAG", "failed: $it")
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.d("TAG", "RESULT_CANCELED")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        preferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
        preferencesEditor = preferences.edit()

//        binding.signInBtn.setSize(SignInButton.SIZE_WIDE)
        binding.signInBtn.setBounceClickListener {
            viewModel.googleSignIn(this,resultLauncher){msg ->
                Log.d("TAG", "onCreate: $msg")
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
        binding.signUpBtn.setBounceClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }
    }
}