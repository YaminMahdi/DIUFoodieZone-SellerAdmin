package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class GetOrderInfo @Inject constructor (
    val repo: SellerRepo
) {
    operator fun invoke(
        orderId: String, path: String, success :(orderInfo: OrderInfo) -> Unit, failed :(msg : String) -> Unit
    ) {
        if(orderId.isNotEmpty())
            repo.getOrderInfo(orderId, path, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}