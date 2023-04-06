package com.diu.mlab.foodie.admin.presentation.main.seller

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.mlab.foodie.admin.domain.model.FoodItem
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
    val myProfile = savedStateHandle.getLiveData<FoodItem>("myProfile")


    fun getFoodList(email: String, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            useCases.getFoodList(email,
                {
                    val tmp =  it.toList()
                    savedStateHandle["foodList"] = tmp
            },failed)
        }
    }

}
