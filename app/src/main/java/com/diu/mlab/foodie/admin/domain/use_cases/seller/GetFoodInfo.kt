package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class GetFoodInfo @Inject constructor (
    val repo: SellerRepo
        ) {
    operator fun invoke(foodId: String, success: (food : FoodItem) -> Unit, failed :(msg : String) -> Unit)=
        repo.getFoodInfo(foodId, success, failed)
}