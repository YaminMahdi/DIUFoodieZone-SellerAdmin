package com.diu.mlab.foodi.admin.domain.use_cases.admin

import com.diu.mlab.foodi.admin.data.repo.AdminRepoImpl
import com.diu.mlab.foodi.admin.domain.model.SuperUser
import com.diu.mlab.foodi.admin.domain.repo.AdminRepo
import javax.inject.Inject

class GetMyProfile @Inject constructor(
    private val repo: AdminRepo
) {
    operator fun invoke(email: String, success :(superUserList: SuperUser) -> Unit, failed :(msg : String) -> Unit) = repo.getMyProfile(email, success, failed)
}