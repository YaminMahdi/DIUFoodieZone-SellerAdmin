package com.diu.mlab.foodi.admin.domain.use_cases.admin

data class AdminUseCases(
    val getMyProfile: GetMyProfile,
    val getSuperUserList: GetSuperUserList,
    val changeSuperUserStatus: ChangeSuperUserStatus
)
