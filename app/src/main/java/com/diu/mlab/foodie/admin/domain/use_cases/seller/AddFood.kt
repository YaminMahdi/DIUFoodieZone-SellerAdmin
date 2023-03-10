package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class AddFood @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke(foodItem: FoodItem, email: String, success :() -> Unit, failed :(msg : String) -> Unit)=repo.addFood(foodItem, email, success, failed)
}