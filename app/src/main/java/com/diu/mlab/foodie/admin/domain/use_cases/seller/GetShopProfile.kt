package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class GetShopProfile @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke( email: String, success :(shopInfo:ShopInfo) -> Unit, failed :(msg : String) -> Unit)=repo.getShopProfile( email, success, failed)
}