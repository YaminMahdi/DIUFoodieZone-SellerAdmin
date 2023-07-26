package com.diu.mlab.foodie.admin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderInfo(
    var orderId: String = "",
    var userInfo: FoodieUser = FoodieUser(),
    var shopInfo: ShopInfo = ShopInfo(),
    var foodInfo: FoodItem = FoodItem(),
    var runnerInfo: FoodieUser = FoodieUser(), //object
    var quantity: Int = 0,
    var type: String = "",
    var typePrice: Int = 0,
    var deliveryCharge: Int = 0,
    var paymentType: String = "",

    var isOrdered: Boolean = false,
    var isPaid: Boolean = false,//0
    var isPaymentConfirmed: Boolean = false,//1
    var isRunnerChosen: Boolean = false,//2
    var isFoodHandover2RunnerNdPaid: Boolean = false,//3
    var isRunnerReceivedFoodnMoney: Boolean = false,//4
    var isFoodHandover2User: Boolean = false,//3
    var isUserReceived: Boolean = false,//5
    var isCanceled: Boolean = false,//6

    var orderTime: Long = 0L,
    var paymentTime: Long = 0L,
    var paymentConfirmationTime: Long = 0L,
    var runnerChosenTime: Long = 0L,
    var foodHandover2RunnerTime: Long = 0L,
    var runnerReceivedTime: Long = 0L,
    var foodHandover2UserTime: Long = 0L,
    var userReceivedTime: Long = 0L,
    var canceledTime: Long = 0L,
): Parcelable