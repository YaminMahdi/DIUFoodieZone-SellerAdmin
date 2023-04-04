package com.diu.mlab.foodie.admin.domain.model

import android.opengl.Visibility
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class SuperUser(
    val nm: String="",
    val email: String="",
    val phone: String="",
    val userType: String="",
    val status: String="",
    val pic: String="",
    val cover: String="",
    val loc: String=""

) : Parcelable{
    fun toShopInfo(visible: Boolean) = ShopInfo(
        nm = nm,
        email = email,
        phone = phone,
        pic = pic,
        cover = cover,
        loc = loc,
        visible = visible
    )
}

