package com.diu.mlab.foodie.admin.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import com.diu.mlab.foodie.admin.util.copyUriToFile
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SellerRepoImpl(
    private val realtime: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context
) : SellerRepo {

    @OptIn(DelicateCoroutinesApi::class)
    override fun addFood(
        foodItem: FoodItem,
        email: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopRef = storage.reference.child("shop/${email}")
        val ref = realtime.getReference("shopProfile").child(email).child("foodList")
        val key = ref.push().key!!

        GlobalScope.launch(Dispatchers.IO){
            var pic = context.copyUriToFile(Uri.parse(foodItem.pic))
            pic = Compressor.compress(context, pic) {
                default(height = 420, width = 420, format = Bitmap.CompressFormat.JPEG)
            }
            val picLink = shopRef.child("$key/pic.jpg")
                .putFile(Uri.fromFile(pic)).await().storage.downloadUrl.await()
            ref.child(key)
                .setValue(foodItem.copy(key = key, pic = picLink.toString()))
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

    override fun removeFood(
        foodId: String,
        email: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        realtime.getReference("shopProfile").child(email).child("foodList").child(foodId).removeValue()
            .addOnSuccessListener {
                Log.d("TAG", "Success")
                success.invoke()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }
    }

    override fun updateFood(
        foodItem: FoodItem,
        email: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        realtime.getReference("shopProfile").child(email).child("foodList").child(foodItem.key).setValue(foodItem)
            .addOnSuccessListener {
                Log.d("TAG", "Success")
                success.invoke()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }
    }

    override fun getFoodList(
        email: String,
        success: (List<FoodItem>) -> Unit,    failed: (msg: String) -> Unit
    ) {
        val foodItemList = mutableListOf<FoodItem>()
        realtime
            .getReference("shopProfile").child(email).child("foodList")
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val food = snapshot.getValue<FoodItem>()!!
                    foodItemList.add(food)
                    success.invoke(foodItemList)
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getShopProfile(
        email: String,
        success: (shopInfo: ShopInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        realtime
            .getReference("shopProfile").child(email).child("info")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get Post object and use the values to update the UI
                        val shopInfo = dataSnapshot.getValue<ShopInfo>()!!
                        Log.d("TAG", shopInfo.toString())
                        success.invoke(shopInfo)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                    }
                }
            )

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun updateShopProfile(
        shopInfo: ShopInfo,
        logoUpdated: Boolean,
        coverUpdated: Boolean,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopRef = storage.reference.child("shop/${shopInfo.email}")
        val path = firestore.collection("superUserProfiles").document(shopInfo.email)

        var tmpShopInfo = shopInfo

        GlobalScope.launch(Dispatchers.IO){
            if(logoUpdated) {
                var logo = context.copyUriToFile(Uri.parse(shopInfo.pic))
                logo = Compressor.compress(context, logo) {
                    default(height = 360, width = 360, format = Bitmap.CompressFormat.JPEG)
                }
                val logoLink = shopRef.child("logo.jpg")
                    .putFile(Uri.fromFile(logo)).await().storage.downloadUrl.await()
                tmpShopInfo = shopInfo.copy(pic = logoLink.toString())
            }
            if(coverUpdated) {
                var cover = context.copyUriToFile(Uri.parse(shopInfo.cover))
                cover = Compressor.compress(context, cover) {
                    default(width = 1080, format = Bitmap.CompressFormat.JPEG)
                }
                val coverLink = shopRef.child("cover.jpg")
                    .putFile(Uri.fromFile(cover)).await().storage.downloadUrl.await()
                tmpShopInfo = shopInfo.copy( cover = coverLink.toString())

            }
            realtime.getReference("shopProfile").child(shopInfo.email).child("info")
                .setValue(tmpShopInfo)
                .addOnSuccessListener {
                    Log.d("TAG", "Success")
                    success.invoke()
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                    failed.invoke("Something went wrong")
                }
            path.get().addOnSuccessListener { document ->
                    if (document.data != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        val superUser = document.toObject(SuperUser::class.java)!!

                        path.set(superUser.margeFromShopInfo(tmpShopInfo))
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully written!")
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }
                    } else {
                        Log.d("TAG", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }

    }
}