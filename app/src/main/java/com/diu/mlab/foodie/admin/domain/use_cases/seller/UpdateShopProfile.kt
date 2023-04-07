package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class UpdateShopProfile @Inject constructor (
    val repo: SellerRepo
        ) {
    operator fun invoke(shopInfo: ShopInfo, logoUpdated: Boolean, coverUpdated: Boolean, success :() -> Unit, failed :(msg : String) -> Unit)=
        repo.updateShopProfile(shopInfo, logoUpdated, coverUpdated, success, failed)
}