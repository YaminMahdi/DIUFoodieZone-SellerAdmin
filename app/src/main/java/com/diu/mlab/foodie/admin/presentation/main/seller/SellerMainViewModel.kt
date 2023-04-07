package com.diu.mlab.foodie.admin.presentation.main.seller

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.use_cases.seller.SellerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMainViewModel @Inject constructor(
    private val useCases: SellerUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    val foodList = savedStateHandle.getLiveData<List<FoodItem>>("foodList")
    val myShopProfile = savedStateHandle.getLiveData<ShopInfo>("myShopProfile")


    fun getFoodList(email: String, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.getFoodList(email,
                {
                    val tmp =  it.toList()
                    savedStateHandle["foodList"] = tmp
            },failed)
        }
    }

    fun addFood(foodItem: FoodItem, email: String, success :() -> Unit, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.addFood(foodItem,email, success,failed)
        }
    }

    fun getShopProfile(email: String, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.getShopProfile(email, {
                savedStateHandle["myShopProfile"] = it
            },failed)
        }
    }

    fun updateShopProfile(shopInfo: ShopInfo, logoUpdated: Boolean, coverUpdated: Boolean, success :() -> Unit, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.updateShopProfile(shopInfo, logoUpdated, coverUpdated, success, failed)
        }
    }

}
