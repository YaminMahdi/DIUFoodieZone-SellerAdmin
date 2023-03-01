package com.diu.mlab.foodi.admin.domain.use_cases.seller

import com.diu.mlab.foodi.admin.domain.model.FoodItem
import com.diu.mlab.foodi.admin.domain.model.ShopInfo
import com.diu.mlab.foodi.admin.domain.repo.SellerRepo
import javax.inject.Inject

class GetShopProfile @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke( email: String, success :(shopInfo:ShopInfo) -> Unit, failed :(msg : String) -> Unit)=repo.getShopProfile( email, success, failed)
}