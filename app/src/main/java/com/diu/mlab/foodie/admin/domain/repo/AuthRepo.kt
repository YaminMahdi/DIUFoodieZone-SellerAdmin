package com.diu.mlab.foodie.admin.domain.repo

import android.app.Activity
import com.diu.mlab.foodie.admin.domain.RequestState
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

interface AuthRepo {
    fun firebaseLogin(credential: GoogleIdTokenCredential, success :(superUser: SuperUser) -> Unit, failed :(msg : String) -> Unit)

    fun firebaseSignup(credential: GoogleIdTokenCredential,superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit)

    suspend fun googleSignIn(activity: Activity, isAuthorized : Boolean): RequestState<GoogleIdTokenCredential>

}