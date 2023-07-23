package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class UpdateShopProfile @Inject constructor (
    val repo: SellerRepo
        ) {
    operator fun invoke(
        shopInfo: ShopInfo, logoUpdated: Boolean, coverUpdated: Boolean, qrUpdated: Boolean, success :() -> Unit, failed :(msg : String) -> Unit
    ) {
        if(shopInfo.nm.isEmpty())
            failed.invoke("You must add Name.")
        else if(shopInfo.phone.isEmpty())
            failed.invoke("You must add Phone Number.")
        else if(shopInfo.loc.isEmpty())
            failed.invoke("You must add Location.")
        else if(shopInfo.pic.isEmpty())
            failed.invoke("You must add Shop Logo.")
        else if(shopInfo.cover.isEmpty())
            failed.invoke("You must add Shop Cover.")
        else
            repo.updateShopProfile(shopInfo, logoUpdated, coverUpdated, qrUpdated, success, failed)
    }
}