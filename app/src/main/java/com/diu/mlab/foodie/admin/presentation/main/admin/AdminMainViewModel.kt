package com.diu.mlab.foodie.admin.presentation.main.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.use_cases.admin.AdminUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val mainUseCases: AdminUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val superUserList = savedStateHandle.getLiveData<List<SuperUser>>("superUserList")
    val adminList = savedStateHandle.getLiveData<List<SuperUser>>("adminList")
    val myProfile = savedStateHandle.getLiveData<SuperUser>("myProfile")

    fun getSuperUserList(type: String, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            mainUseCases.getSuperUserList(type, {
                val tmp = it.toList()
                savedStateHandle["superUserList"]=tmp
            }, failed)
        }
    }

    fun getActiveAdminList(failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            mainUseCases.getSuperUserList("accepted", {
                val adminList = mutableListOf<SuperUser>()
                it.forEach { usr ->
                    if(usr.userType=="admin")
                        adminList.add(usr)
                }
                savedStateHandle["adminList"]=adminList
            }, failed)
        }
    }

    fun changeSuperUserList(superUserList: List<SuperUser>){
        val tmp = superUserList.toList()
        savedStateHandle["superUserList"]=tmp
    }

    fun changeSuperUserStatus(superUser: SuperUser, success :() -> Unit, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            mainUseCases.changeSuperUserStatus(superUser, success, failed)
        }
    }

    fun getMyProfile(email: String, failed :(msg : String) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            mainUseCases.getMyProfile(email,
            {
//                val tmp = it.copy()
                savedStateHandle["myProfile"]=it
            }, failed)
        }
    }

    fun updateAdminProfile(
        admin: SuperUser,
        picUpdated: Boolean,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO){
            mainUseCases.updateAdminProfile(admin,picUpdated, success, failed)
        }
    }

}