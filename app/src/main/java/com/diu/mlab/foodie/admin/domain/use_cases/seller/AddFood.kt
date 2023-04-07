package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import javax.inject.Inject

class AddFood @Inject constructor (
    val repo: SellerRepo
    ) {
    operator fun invoke(foodItem: FoodItem, email: String, success :() -> Unit, failed :(msg : String) -> Unit){
        if(foodItem.nm.isEmpty())
            failed.invoke("You must add food name.")
        else if(foodItem.price.isEmpty())
            failed.invoke("You must add food price.")
        else if(foodItem.time.isEmpty())
            failed.invoke("You must add time.")
        else if(foodItem.status.isEmpty())
            failed.invoke("You must add availability.")
        else if(foodItem.pic.isEmpty())
            failed.invoke("You must add food picture.")
        else
            repo.addFood(foodItem, email, success, failed)
    }
}