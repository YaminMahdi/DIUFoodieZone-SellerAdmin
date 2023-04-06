package com.diu.mlab.foodie.admin.domain.use_cases.auth

import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import com.google.android.gms.auth.api.identity.SignInCredential
import javax.inject.Inject

class FirebaseSignup @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(credential: SignInCredential,superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit) {
        if(superUser.pic.isNotEmpty() &&
            (superUser.cover.isNotEmpty() || superUser.loc.isNotEmpty()) &&
            superUser.nm.isNotEmpty() &&
            superUser.phone.isNotEmpty()
        ){
            repo.firebaseSignup(credential, superUser, success, failed)
        }
        else failed.invoke("Fill all input.")
    }
}