package com.diu.mlab.foodie.admin.domain.use_cases.admin

import javax.inject.Inject

data class AdminUseCases @Inject constructor(
    val getMyProfile: GetMyProfile,
    val getSuperUserList: GetSuperUserList,
    val changeSuperUserStatus: ChangeSuperUserStatus,
    val updateAdminProfile: UpdateAdminProfile
)
