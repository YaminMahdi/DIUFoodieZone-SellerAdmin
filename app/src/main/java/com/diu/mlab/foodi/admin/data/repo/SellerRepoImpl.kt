package com.diu.mlab.foodi.admin.data.repo

import android.util.Log
import com.diu.mlab.foodi.admin.domain.model.FoodItem
import com.diu.mlab.foodi.admin.domain.model.ShopInfo
import com.diu.mlab.foodi.admin.domain.repo.SellerRepo
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SellerRepoImpl @Inject constructor(
    private val realtime: FirebaseDatabase
    ) : SellerRepo {

    override fun addFood(
        foodItem: FoodItem,
        email: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val ref = realtime.getReference("shopProfile").child(email).child("foodList")

        val key = ref.push().key!!

        ref.child(key).setValue(foodItem.copy(key = key))
            .addOnSuccessListener {
                Log.d("TAG", "Success")
                success.invoke()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }
    }

    override fun removeFood(
        key: String,
        email: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        realtime.getReference("shopProfile").child(email).child("foodList").child(key).removeValue()
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
        success: (List<FoodItem>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val foodItemList = mutableListOf<FoodItem>()
        realtime
            .getReference("shopProfile").child(email).child("foodList")
            .addChildEventListener(
                object: ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val food = snapshot.getValue<FoodItem>()!!
                        foodItemList.add(food)
                        success.invoke(foodItemList)
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                }
            )
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

    override fun updateShopProfile(
        shopInfo: ShopInfo,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        realtime.getReference("shopProfile").child(shopInfo.email).child("foodList").child("info").setValue(shopInfo)
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