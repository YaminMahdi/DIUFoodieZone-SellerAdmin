package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.SignInButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
            viewModel.firebaseLogin(credential,{
                Log.d("TAG", "success:")
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

        binding.signInBtn.setSize(SignInButton.SIZE_WIDE)
        binding.signInBtn.setOnClickListener {
            viewModel.googleSignIn(this,resultLauncher){msg ->
                Log.d("TAG", "onCreate: $msg")
            }
        }
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }






    }


}