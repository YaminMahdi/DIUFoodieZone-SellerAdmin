package com.diu.mlab.foodie.admin.domain.model

import android.opengl.Visibility
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopInfo(
    val nm: String= "",
    val email: String= "",
    val phone: String= "",
    val pic: String= "",
    val cover: String= "",
    val loc: String= "",
    val visible: Boolean= false,
): Parcelable
