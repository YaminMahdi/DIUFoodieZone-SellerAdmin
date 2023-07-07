package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class GetMyOrderList @Inject constructor (
    val repo: SellerRepo
) {
    operator fun invoke(
        path: String, success :(orderInfoList: List<OrderInfo>) -> Unit, failed :(msg : String) -> Unit
    ) {
        if(Firebase.auth.currentUser != null)
            repo.getMyOrderList(path, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}