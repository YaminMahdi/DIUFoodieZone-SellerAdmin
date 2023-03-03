package com.diu.mlab.foodie.admin.domain.use_cases.admin

import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AdminRepo
import javax.inject.Inject

class GetSuperUserList @Inject constructor(
    private val repo: AdminRepo
) {
    operator fun invoke(type: String, success :(superUserList: List<SuperUser>) -> Unit, failed :(msg : String) -> Unit) = repo.getSuperUserList(type, success, failed)
}