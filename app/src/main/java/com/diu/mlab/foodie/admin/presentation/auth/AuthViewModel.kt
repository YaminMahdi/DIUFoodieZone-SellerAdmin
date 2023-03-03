package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.diu.mlab.foodi.admin.domain.use_cases.auth.AuthUseCases
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    fun googleSignIn(
        activity: Activity,
        resultLauncher : ActivityResultLauncher<IntentSenderRequest>,
        failed : (msg : String) -> Unit
    ){
        authUseCases.googleSignIn(activity,resultLauncher, failed)
    }

    fun firebaseLogin(
        credential: SignInCredential, success :() -> Unit, failed :(msg : String) -> Unit
    ){
        authUseCases.firebaseLogin(credential, success, failed)
    }
}