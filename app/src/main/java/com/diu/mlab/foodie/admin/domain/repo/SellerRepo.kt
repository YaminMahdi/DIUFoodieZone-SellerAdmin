package com.diu.mlab.foodie.admin.domain.repo

import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.domain.model.ShopInfo

interface SellerRepo {

    fun addFood(foodItem: FoodItem,  success :() -> Unit, failed :(msg : String) -> Unit)

    fun removeFood(foodId: String, success :() -> Unit, failed :(msg : String) -> Unit)

    fun updateFood(foodItem: FoodItem, success :() -> Unit, failed :(msg : String) -> Unit)

    fun getFoodList(success :(List<FoodItem>) -> Unit, failed :(msg : String) -> Unit)

    fun getShopProfile(success :(shopInfo: ShopInfo) -> Unit, failed :(msg : String) -> Unit)

    fun updateShopProfile(shopInfo: ShopInfo, logoUpdated: Boolean, coverUpdated: Boolean, success :() -> Unit, failed :(msg : String) -> Unit)

    fun getFoodInfo(foodId: String, success: (food : FoodItem) -> Unit, failed :(msg : String) -> Unit)

    fun getMyOrderList(path: String, success :(orderInfoList: List<OrderInfo>) -> Unit, failed :(msg : String) -> Unit)

    fun getOrderInfo(orderId: String, path: String, success :(orderInfo: OrderInfo) -> Unit, failed :(msg : String) -> Unit)

    fun updateOrderInfo(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        userEmail: String,
        success :() -> Unit,
        failed :(msg : String) -> Unit
    )

}