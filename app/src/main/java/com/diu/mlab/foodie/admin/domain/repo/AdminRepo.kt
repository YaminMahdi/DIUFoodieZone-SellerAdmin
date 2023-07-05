package com.diu.mlab.foodie.admin.domain.repo

import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.model.SuperUser

interface AdminRepo {

    fun getSuperUserList(type: String, success :(superUserList: List<SuperUser>) -> Unit, failed :(msg : String) -> Unit)

    fun getMyProfile(email: String, success :(superUser: SuperUser) -> Unit, failed :(msg : String) -> Unit)

    fun changeSuperUserStatus(superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit)

    fun updateAdminProfile(admin: SuperUser, picUpdated: Boolean, success :() -> Unit, failed :(msg : String) -> Unit)


}