package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GetMyOrderList @Inject constructor (
    val repo: SellerRepo,
    private val firebaseUser: FirebaseUser?
) {
    operator fun invoke(
        path: String, success :(orderInfoList: List<OrderInfo>) -> Unit, failed :(msg : String) -> Unit
    ) {
        if(firebaseUser != null)
            repo.getMyOrderList(path, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}