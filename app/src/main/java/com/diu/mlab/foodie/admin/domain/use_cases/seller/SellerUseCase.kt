package com.diu.mlab.foodie.admin.domain.use_cases.seller

import javax.inject.Inject

data class SellerUseCase @Inject constructor(
    val addFood: AddFood,
    val getFoodList: GetFoodList,
    val getShopProfile: GetShopProfile,
    val removeFood: RemoveFood,
    val updateFood: UpdateFood,
    val updateShopProfile: UpdateShopProfile
)
