package com.diu.mlab.foodi.admin.domain.use_cases.admin

import com.diu.mlab.foodi.admin.data.repo.AdminRepoImpl
import com.diu.mlab.foodi.admin.domain.model.SuperUser
import com.diu.mlab.foodi.admin.domain.repo.AdminRepo
import javax.inject.Inject

class GetSuperUserList @Inject constructor(
    private val repo: AdminRepo
) {
    operator fun invoke(type: String, success :(superUserList: List<SuperUser>) -> Unit, failed :(msg : String) -> Unit) = repo.getSuperUserList(type, success, failed)
}