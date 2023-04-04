package com.diu.mlab.foodie.admin.domain.use_cases.auth

import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val firebaseLogin: FirebaseLogin,
    val firebaseSignup: FirebaseSignup,
    val googleSignIn: GoogleSignIn
)
