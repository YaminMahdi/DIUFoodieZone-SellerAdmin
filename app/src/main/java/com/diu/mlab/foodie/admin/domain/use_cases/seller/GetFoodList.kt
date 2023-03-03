package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class GetFoodList @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke( email: String, success :(List<FoodItem>) -> Unit, failed :(msg : String) -> Unit)=repo.getFoodList( email, success, failed)
}