package com.diu.mlab.foodie.admin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItem (
    val foodId : String="",
    val nm : String="",
    val pic : String="",
    val price : String="",
    val time: String="",
    val status : String="Unavailable",
    val types : String=""
): Parcelable

