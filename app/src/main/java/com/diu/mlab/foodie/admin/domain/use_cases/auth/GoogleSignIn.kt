package com.diu.mlab.foodie.admin.domain.use_cases.auth

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import javax.inject.Inject

class GoogleSignIn @Inject constructor (
    val repo: AuthRepo
) {
    operator fun invoke(
        superUser: SuperUser?,
        activity: Activity,
        resultLauncher : ActivityResultLauncher<IntentSenderRequest>,
        failed :(msg : String) -> Unit
    ) {
        if(superUser != null){
            if(superUser.nm.isEmpty())
                failed.invoke("You must add Name.")
            else if(superUser.phone.isEmpty())
                failed.invoke("You must add Phone Number.")
            else if(superUser.loc.isEmpty() && superUser.userType == "admin")
                failed.invoke("You must add Work Place.")
            else if(superUser.userType == "shop"){
                if(superUser.loc.isEmpty())
                    failed.invoke("You must add Location.")
                else if(superUser.pic.isEmpty())
                    failed.invoke("You must add Shop Logo.")
                else if(superUser.cover.isEmpty())
                    failed.invoke("You must add Shop Cover.")
                else
                    repo.googleSignIn(activity, resultLauncher, failed)
            }
            else
                repo.googleSignIn(activity, resultLauncher, failed)
        }
        else
            repo.googleSignIn(activity, resultLauncher, failed)

    }
}