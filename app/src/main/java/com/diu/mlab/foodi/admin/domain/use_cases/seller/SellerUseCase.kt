package com.diu.mlab.foodi.admin.domain.use_cases.seller

data class SellerUseCase(
    val addFood: AddFood,
    val getFoodList: GetFoodList,
    val getShopProfile: GetShopProfile,
    val removeFood: RemoveFood,
    val updateFood: UpdateFood,
    val updateShopProfile: UpdateShopProfile
)
