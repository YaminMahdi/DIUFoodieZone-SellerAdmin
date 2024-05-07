package com.diu.mlab.foodie.admin.domain.use_cases.auth

import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

class FirebaseLogin @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(credential: GoogleIdTokenCredential, success :(superUser: SuperUser) -> Unit, failed :(msg : String) -> Unit)=repo.firebaseLogin(credential, success, failed)
}