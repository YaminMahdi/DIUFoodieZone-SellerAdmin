package com.diu.mlab.foodie.admin.domain.use_cases.seller

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import com.diu.mlab.foodie.admin.util.hasAlphabet
import javax.inject.Inject

class UpdateFood @Inject constructor (
    val repo: SellerRepo
        ) {
    operator fun invoke(foodItem: FoodItem,success :() -> Unit, failed :(msg : String) -> Unit){
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
        else if(foodItem.price.hasAlphabet())
            failed.invoke("Price can't contain Alphabets.")
        else if(foodItem.types.split(",").size != foodItem.price.split(",").size )
            failed.invoke("Missing all types or prices.")
        else
            repo.updateFood(foodItem, success, failed)
    }

}