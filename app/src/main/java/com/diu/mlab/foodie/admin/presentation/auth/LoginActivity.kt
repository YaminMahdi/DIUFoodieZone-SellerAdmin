package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diu.mlab.foodie.admin.R
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE_GOOGLE_SIGN_IN = 69 /* unique request id */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewModel : AuthViewModel by viewModels()
        getString(R.string.server_client_id)
        viewModel.googleSignIn(this){msg ->
            Log.d("TAG", "onCreate: $msg")
        }


        var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val credential: SignInCredential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)

                TODO("doSomeOperations()")
            }
        }


    }


}