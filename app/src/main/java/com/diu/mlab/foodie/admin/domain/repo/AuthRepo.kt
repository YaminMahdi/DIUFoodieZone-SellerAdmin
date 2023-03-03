package com.diu.mlab.foodie.admin.domain.repo

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.google.android.gms.auth.api.identity.SignInCredential

interface AuthRepo {
    fun firebaseLogin(credential: SignInCredential, success :() -> Unit, failed :(msg : String) -> Unit)

    fun firebaseSignup(credential: SignInCredential,superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit)

    fun googleSignIn(
        activity: Activity,
        resultLauncher : ActivityResultLauncher<IntentSenderRequest>,
        failed :(msg : String) -> Unit
    )


}