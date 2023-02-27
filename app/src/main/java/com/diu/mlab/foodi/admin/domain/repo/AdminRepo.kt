package com.diu.mlab.foodi.admin.domain.repo

import com.diu.mlab.foodi.admin.domain.model.SuperUser

interface AdminRepo {

    fun getSuperUserList(type: String, success :(superUserList: List<SuperUser>) -> Unit, failed :(msg : String) -> Unit)

    fun getMyProfile(email: String, success :(superUser: SuperUser) -> Unit, failed :(msg : String) -> Unit)

    fun changeSuperUserStatus(email: String, superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit)

}