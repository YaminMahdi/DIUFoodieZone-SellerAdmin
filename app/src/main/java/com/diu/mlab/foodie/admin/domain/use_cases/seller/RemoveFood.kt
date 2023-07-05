package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class RemoveFood @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke(foodId: String,success :() -> Unit, failed :(msg : String) -> Unit)=repo.removeFood(foodId, success, failed)
}