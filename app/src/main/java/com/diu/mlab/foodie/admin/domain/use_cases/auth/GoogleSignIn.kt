package com.diu.mlab.foodie.admin.domain.use_cases.auth

import android.app.Activity
import com.diu.mlab.foodie.admin.domain.RequestState
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

class GoogleSignIn @Inject constructor (
    val repo: AuthRepo
) {
    suspend operator fun invoke(
        superUser: SuperUser?,
        activity: Activity
    ): RequestState<GoogleIdTokenCredential> {
        return if(superUser != null){
            if(superUser.nm.isEmpty())
                RequestState.Error("You must add Name.")
            else if(superUser.phone.isEmpty())
                RequestState.Error("You must add Phone Number.")
            else if(superUser.loc.isEmpty() && superUser.userType == "admin")
                RequestState.Error("You must add Work Place.")
            else if(superUser.userType == "shop"){
                if(superUser.loc.isEmpty())
                    RequestState.Error("You must add Location.")
                else if(superUser.pic.isEmpty())
                    RequestState.Error("You must add Shop Logo.")
                else if(superUser.cover.isEmpty())
                    RequestState.Error("You must add Shop Cover.")
                else
                    repo.googleSignIn(activity, false)
            }
            else
                repo.googleSignIn(activity, false)
        }
        else //login
            repo.googleSignIn(activity, true)

    }
}