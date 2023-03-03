package com.diu.mlab.foodi.admin.domain.use_cases.admin

import android.provider.ContactsContract.CommonDataKinds.Email
import com.diu.mlab.foodi.admin.data.repo.AdminRepoImpl
import com.diu.mlab.foodi.admin.domain.model.SuperUser
import com.diu.mlab.foodi.admin.domain.repo.AdminRepo
import javax.inject.Inject

class ChangeSuperUserStatus @Inject constructor(
    private val repo: AdminRepo
) {
    operator fun invoke(email: String,superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit) = repo.changeSuperUserStatus(email,superUser, success, failed)
}