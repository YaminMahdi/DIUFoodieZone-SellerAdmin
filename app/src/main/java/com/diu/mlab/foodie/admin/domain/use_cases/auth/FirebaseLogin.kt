package com.diu.mlab.foodie.admin.domain.use_cases.auth

import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import com.google.android.gms.auth.api.identity.SignInCredential
import javax.inject.Inject

class FirebaseLogin @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(credential: SignInCredential, success :() -> Unit, failed :(msg : String) -> Unit)=repo.firebaseLogin(credential, success, failed)
}