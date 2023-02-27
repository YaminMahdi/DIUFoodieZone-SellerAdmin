package com.diu.mlab.foodi.admin.presentation.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.diu.mlab.foodi.admin.R
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential


class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE_GOOGLE_SIGN_IN = 69 /* unique request id */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val credential: SignInCredential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)

                TODO("doSomeOperations()")
            }
        }


    }


}