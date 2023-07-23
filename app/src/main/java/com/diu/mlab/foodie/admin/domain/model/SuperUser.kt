package com.diu.mlab.foodie.admin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class SuperUser(
    val nm: String="",
    val email: String="",
    val phone: String="",
    val paymentType: String = "",
    val userType: String="",
    val status: String="",
    val pic: String="",
    val qr: String="",
    val cover: String="",
    val loc: String=""

) : Parcelable{
    fun toShopInfo(visible: Boolean) = ShopInfo(
        nm = nm,
        email = email,
        phone = phone,
        paymentType = paymentType,
        pic = pic,
        cover = cover,
        qr = qr,
        loc = loc,
        visible = visible
    )

    fun margeFromShopInfo(shopInfo: ShopInfo) = SuperUser(
        nm = shopInfo.nm,
        email = this.email,
        phone = shopInfo.phone,
        userType = this.userType,
        paymentType = shopInfo.paymentType,
        status = this.status,
        pic = shopInfo.pic,
        cover = shopInfo.cover,
        qr = shopInfo.qr,
        loc = shopInfo.loc
    )

}

