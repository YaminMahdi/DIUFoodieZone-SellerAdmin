package com.diu.mlab.foodie.admin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class FoodieUser(
    var id: String="",
    var nm: String="",
    var email: String="",
    var phone: String="",
    var userType: String="",
    var pic: String="",
    var loc: String=""
) : Parcelable

