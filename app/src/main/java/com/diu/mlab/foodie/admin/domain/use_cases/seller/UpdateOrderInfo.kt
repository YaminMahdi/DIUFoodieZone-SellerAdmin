package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class UpdateOrderInfo @Inject constructor (
    val repo: SellerRepo
) {
    operator fun invoke(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        userEmail: String,
        success :() -> Unit,
        failed :(msg : String) -> Unit
    ) {
        if(orderId.isNotEmpty())
            repo.updateOrderInfo(orderId, varBoolName, value, varTimeName, userEmail, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}