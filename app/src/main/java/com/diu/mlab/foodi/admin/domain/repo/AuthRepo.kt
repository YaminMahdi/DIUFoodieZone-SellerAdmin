package com.diu.mlab.foodi.admin.domain.repo

import android.app.Activity
import com.diu.mlab.foodi.admin.domain.model.SuperUser
import com.google.android.gms.auth.api.identity.SignInCredential

interface AuthRepo {
    fun firebaseLogin(credential: SignInCredential, success :() -> Unit, failed :(msg : String) -> Unit)

    fun firebaseSignup(credential: SignInCredential,superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit)

    fun googleSignIn(activity: Activity, failed :(msg : String) -> Unit)



}