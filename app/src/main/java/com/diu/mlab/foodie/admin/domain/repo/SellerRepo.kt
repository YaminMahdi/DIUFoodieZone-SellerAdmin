package com.diu.mlab.foodie.admin.domain.repo

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.ShopInfo

interface SellerRepo {

    fun addFood(foodItem: FoodItem, email: String, success :() -> Unit, failed :(msg : String) -> Unit)

    fun removeFood(foodId: String, email: String, success :() -> Unit, failed :(msg : String) -> Unit)

    fun updateFood(foodItem: FoodItem, email: String, success :() -> Unit, failed :(msg : String) -> Unit)

    fun getShopProfile(email: String, success :(shopInfo: ShopInfo) -> Unit, failed :(msg : String) -> Unit)

    fun updateShopProfile(shopInfo: ShopInfo, success :() -> Unit, failed :(msg : String) -> Unit)



}