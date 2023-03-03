package com.diu.mlab.foodi.admin.domain.use_cases.seller

import com.diu.mlab.foodi.admin.domain.model.FoodItem
import com.diu.mlab.foodi.admin.domain.repo.SellerRepo
import javax.inject.Inject

class UpdateFood @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke(foodItem: FoodItem, email: String, success :() -> Unit, failed :(msg : String) -> Unit)=repo.updateFood(foodItem, email, success, failed)
}