package com.diu.mlab.foodie.admin.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AdminRepo
import com.diu.mlab.foodie.admin.util.copyUriToFile
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val realtime: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val context: Context

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
                if (document.data != null) {
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
            }
    }

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

    @OptIn(DelicateCoroutinesApi::class)
    override fun updateAdminProfile(
        admin: SuperUser,
        picUpdated: Boolean,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopRef = storage.reference.child("admin/${admin.email}")
        val path = firestore.collection("superUserProfiles").document(admin.email)

        var tmpAdminInfo = admin

        GlobalScope.launch(Dispatchers.IO){
            if(picUpdated) {
                var pic = context.copyUriToFile(Uri.parse(admin.pic))
                pic = Compressor.compress(context, pic) {
                    default(height = 360, width = 360, format = Bitmap.CompressFormat.JPEG)
                }
                val picLink = shopRef.child("pic.jpg")
                    .putFile(Uri.fromFile(pic)).await().storage.downloadUrl.await()
                tmpAdminInfo = admin.copy(pic = picLink.toString())
            }
            path.set(tmpAdminInfo)
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully written!")
                    success.invoke()
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                    failed.invoke("Something went wrong")
                }
        }
    }
}