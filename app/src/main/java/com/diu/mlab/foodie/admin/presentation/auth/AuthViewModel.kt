package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.diu.mlab.foodi.admin.domain.use_cases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    fun googleSignIn(activity: Activity, failed : (msg : String) -> Unit){
        authUseCases.googleSignIn(activity, failed)
    }
}