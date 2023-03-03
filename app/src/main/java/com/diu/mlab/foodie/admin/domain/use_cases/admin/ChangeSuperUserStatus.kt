package com.diu.mlab.foodie.admin.domain.use_cases.admin

import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AdminRepo
import javax.inject.Inject

class ChangeSuperUserStatus @Inject constructor(
    private val repo: AdminRepo
) {
    operator fun invoke(email: String, superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit) = repo.changeSuperUserStatus(email,superUser, success, failed)
}