package com.diu.mlab.foodie.admin.presentation.main.seller

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.use_cases.seller.SellerUseCase
import com.diu.mlab.foodie.admin.util.toDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMainViewModel @Inject constructor(
    private val useCases: SellerUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    val orderInfo = savedStateHandle.getLiveData("orderInfo", OrderInfo())
    val orderList = savedStateHandle.getLiveData<List<OrderInfo>>("orderList", emptyList())
    val progressInfoList= savedStateHandle.getLiveData("progressInfoList", emptyList<Pair<String, String>>())
    val foodList = savedStateHandle.getLiveData<List<FoodItem>>("foodList", emptyList())
    val myShopProfile = savedStateHandle.getLiveData("myShopProfile", ShopInfo())
    val foodInfo = savedStateHandle.getLiveData("foodInfo", FoodItem())

    var selectedTab
        get()= savedStateHandle.get<Int>("selectedTab")
        set(@IdRes tabId){
            savedStateHandle["selectedTab"] = tabId
        }
    var path
        get()= savedStateHandle["path"] ?: "current"
        set(path){
            savedStateHandle["path"] = path
        }

    private var getOrderListJob : Job? =null
    private var orderInfoJob : Job? =null

    fun getMyOrderList(path: String,failed :(msg : String) -> Unit){
        getOrderListJob?.apply {
            this.cancel()
            savedStateHandle["orderList"] = emptyList<OrderInfo>()
        }
        getOrderListJob = viewModelScope.launch(Dispatchers.IO){
            useCases.getMyOrderList(path, {
                savedStateHandle["orderList"] = it
            },failed)
            useCases.getOrderInfo
        }
    }

    fun getOrderInfo(orderId: String, path: String, failed :(msg : String) -> Unit){
        orderInfoJob?.apply {
            this.cancel()
            savedStateHandle["orderInfo"] = OrderInfo()
        }
        orderInfoJob = viewModelScope.launch(Dispatchers.IO){
            useCases.getOrderInfo(orderId,path,{odrInfo ->
                val tmpList = mutableListOf<Pair<String,String>>()

                savedStateHandle["orderInfo"] = odrInfo
                if(odrInfo.paymentConfirmationTime != 0L) {
                    if(odrInfo.isPaymentConfirmed)
                        tmpList.add(Pair("Pay Confirmed", odrInfo.paymentConfirmationTime.toDateTime()))
                    else
                        tmpList.add(Pair("Payment Failed", odrInfo.paymentConfirmationTime.toDateTime()))
                }
                if(odrInfo.isRunnerChosen)
                    tmpList.add(Pair("Runner Chosen", odrInfo.runnerChosenTime.toDateTime()))
                if(odrInfo.isFoodHandover2RunnerNdPaid)
                    tmpList.add(Pair("Food Given To Runner", odrInfo.foodHandover2RunnerTime.toDateTime()))
                if(odrInfo.runnerReceivedTime != 0L){
                    if(odrInfo.isRunnerReceivedFoodnMoney)
                        tmpList.add(Pair("Runner Got Paid", odrInfo.runnerReceivedTime.toDateTime()))
                    else
                        tmpList.add(Pair("Runner Didn't Got Paid", odrInfo.runnerReceivedTime.toDateTime()))
                }
                if(odrInfo.isFoodHandover2User)
                    tmpList.add(Pair("Food Delivered", odrInfo.foodHandover2UserTime.toDateTime()))
                if(odrInfo.userReceivedTime != 0L) {
                    if(odrInfo.isUserReceived)
                        tmpList.add(Pair("Food Received", odrInfo.userReceivedTime.toDateTime()))
                    else
                        tmpList.add(Pair("Food Not Received", odrInfo.userReceivedTime.toDateTime()))
                }
                if(odrInfo.isCanceled)
                    tmpList.add(Pair("Canceled", odrInfo.canceledTime.toDateTime()))

                savedStateHandle["progressInfoList"]= tmpList
            },failed)
        }
    }


    fun getFoodList(failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.getFoodList(
                {
                    val tmp =  it.toList()
                    Log.d("TAG", "getFoodList: new food")
                    savedStateHandle["foodList"] = tmp
            },failed)
        }
    }

    fun addFood(foodItem: FoodItem, success :() -> Unit, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.addFood(foodItem, success,failed)
        }
    }

    fun getShopProfile(failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.getShopProfile({
                savedStateHandle["myShopProfile"] = it
            },failed)
        }
    }

    fun updateShopProfile(shopInfo: ShopInfo, logoUpdated: Boolean, coverUpdated: Boolean, qrUpdated: Boolean,success :() -> Unit, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.updateShopProfile(shopInfo, logoUpdated, coverUpdated,qrUpdated, success, failed)
        }
    }

    fun getFoodInfo(foodId: String, failed :(msg : String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO){
            useCases.getFoodInfo(foodId,{
                savedStateHandle["foodInfo"] = it
            },failed)
        }
    }

    fun updateFood(foodItem: FoodItem, success :() -> Unit, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.updateFood(foodItem, success, failed)
        }
    }

    fun updateOrderInfo(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        userEmail: String,
        success: () -> Unit, failed :(msg : String) -> Unit
    ) {

        viewModelScope.launch {
            useCases.updateOrderInfo(orderId,varBoolName,value,varTimeName,userEmail,success,failed)
        }
    }

}
