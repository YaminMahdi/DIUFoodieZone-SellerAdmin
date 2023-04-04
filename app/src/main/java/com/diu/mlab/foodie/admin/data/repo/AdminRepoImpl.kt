package com.diu.mlab.foodie.admin.data.repo

import android.util.Log
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AdminRepo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject

class AdminRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val realtime: FirebaseDatabase

) : AdminRepo {
    override fun getSuperUserList(
        type: String,
        success: (superUserList: List<SuperUser>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userList = mutableListOf<SuperUser>()
        firestore.collection("superUserProfiles")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val user = document.toObject<SuperUser>()
                    if(user.status==type){
                        userList.add(user)
                    }
                }
                success.invoke(userList)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }
    }

    override fun getMyProfile(
        email: String,
        success: (superUser: SuperUser) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        firestore.collection("superUserProfiles").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    val superUser = document.toObject(SuperUser::class.java)!!
                    success.invoke(superUser)
                } else {
                    Log.d("TAG", "No such document")
                    failed.invoke("User doesn't exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }    }

    override fun changeSuperUserStatus(
        superUser: SuperUser,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val path =realtime.getReference("shopProfile").child(superUser.email).child("info")
        if(superUser.status=="accepted" && superUser.userType=="shop") {
            path.setValue(superUser.toShopInfo(true))
                .addOnSuccessListener {
                    Log.d("TAG", "Success")
                    success.invoke()
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                    failed.invoke("Something went wrong")
                }
        }
        else if(superUser.status=="rejected" && superUser.userType=="shop") {
            path.child("visible")
                .setValue(false)
                .addOnSuccessListener {
                    Log.d("TAG", "Success")
                    success.invoke()
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                    failed.invoke("Something went wrong")
                }
        }
        firestore.collection("superUserProfiles").document(superUser.email)
            .update("status", superUser.status)
            .addOnSuccessListener {
                Log.d("TAG", "Success")
                success.invoke()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }

    }


}