package com.diu.mlab.foodi.admin.domain.use_cases.auth

import android.app.Activity
import com.diu.mlab.foodi.admin.domain.repo.AuthRepo
import com.google.android.gms.auth.api.identity.SignInCredential
import javax.inject.Inject

class GoogleSignIn @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(activity: Activity, failed :(msg : String) -> Unit)=repo.googleSignIn(activity, failed)
}