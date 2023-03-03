package com.diu.mlab.foodi.admin.domain.use_cases.auth

import com.diu.mlab.foodie.admin.domain.use_cases.auth.FirebaseLogin
import com.diu.mlab.foodie.admin.domain.use_cases.auth.FirebaseSignup
import com.diu.mlab.foodie.admin.domain.use_cases.auth.GoogleSignIn
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val firebaseLogin: FirebaseLogin,
    val firebaseSignup: FirebaseSignup,
    val googleSignIn: GoogleSignIn
)
