package com.diu.mlab.foodie.admin.domain.use_cases.auth

import android.app.Activity
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import javax.inject.Inject

class GoogleSignIn @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(activity: Activity, failed :(msg : String) -> Unit)=repo.googleSignIn(activity, failed)
}