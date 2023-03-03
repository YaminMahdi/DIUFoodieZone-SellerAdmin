package com.diu.mlab.foodi.admin.domain.use_cases.seller

import com.diu.mlab.foodi.admin.domain.model.FoodItem
import com.diu.mlab.foodi.admin.domain.repo.SellerRepo
import java.nio.channels.spi.AbstractSelectionKey
import javax.inject.Inject

class RemoveFood @Inject constructor (
    val repo: SellerRepo
        ) {
    operator  fun invoke(key: String, email: String, success :() -> Unit, failed :(msg : String) -> Unit)=repo.removeFood(key, email, success, failed)
}