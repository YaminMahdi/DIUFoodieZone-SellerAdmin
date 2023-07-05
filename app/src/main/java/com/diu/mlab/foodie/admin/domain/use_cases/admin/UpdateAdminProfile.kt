package com.diu.mlab.foodie.admin.domain.use_cases.admin

import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AdminRepo
import javax.inject.Inject

class UpdateAdminProfile @Inject constructor(
    private val repo: AdminRepo
) {

    operator fun invoke(
        admin: SuperUser,
        picUpdated: Boolean,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) = repo.updateAdminProfile(admin, picUpdated, success, failed)
}