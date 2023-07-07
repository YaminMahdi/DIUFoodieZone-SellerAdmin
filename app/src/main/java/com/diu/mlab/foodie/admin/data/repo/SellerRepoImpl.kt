package com.diu.mlab.foodie.admin.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.SellerRepo
import com.diu.mlab.foodie.admin.util.copyUriToFile
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"
        val shopRef = storage.reference.child("shop/${shopAdminEmail}")
        val ref = realtime.getReference("shopProfile").child(shopAdminEmail).child("foodList")
        val key = ref.push().key!!

        GlobalScope.launch(Dispatchers.IO){
            var pic = context.copyUriToFile(Uri.parse(foodItem.pic))
            pic = Compressor.compress(context, pic) {
                default(height = 420, width = 420, format = Bitmap.CompressFormat.JPEG)
            }
            val picLink = shopRef.child("$key/pic.jpg")
                .putFile(Uri.fromFile(pic)).await().storage.downloadUrl.await()
            ref.child(key)
                .setValue(foodItem.copy(foodId = key, pic = picLink.toString()))
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
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"
        realtime.getReference("shopProfile").child(shopAdminEmail).child("foodList").child(foodId).removeValue()
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
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"
        realtime.getReference("shopProfile").child(shopAdminEmail).child("foodList").child(foodItem.foodId).setValue(foodItem)
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
        success: (List<FoodItem>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        val foodItemList = mutableListOf<FoodItem>()
        realtime
            .getReference("shopProfile").child(shopAdminEmail).child("foodList")
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val food = snapshot.getValue<FoodItem>()!!
                    foodItemList.add(food)
                    success.invoke(foodItemList)
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val updatedList = foodItemList.toMutableList()
                    val updatedData = snapshot.getValue<FoodItem>()!!
                    run block@ {
                        foodItemList.forEachIndexed {i,it->
                            if(it.foodId == updatedData.foodId){
                                updatedList[i] = updatedData
                                Log.d("TAG", "getFoodList: updated food found")
                                return@block
                            }
                        }
                    }
                    Log.d("TAG", "getFoodList: updated food")
                    success.invoke(updatedList)
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getShopProfile(
        success: (shopInfo: ShopInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        realtime
            .getReference("shopProfile").child(shopAdminEmail).child("info")
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
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        val shopRef = storage.reference.child("shop/${shopAdminEmail}")
        val path = firestore.collection("superUserProfiles").document(shopAdminEmail)

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
            realtime.getReference("shopProfile").child(shopAdminEmail).child("info")
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

    override fun getFoodInfo(
        foodId: String,
        success: (food : FoodItem) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        realtime
            .getReference("shopProfile").child(shopAdminEmail).child("foodList").child(foodId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val food = snapshot.getValue<FoodItem>()!!
                        success.invoke(food)
                    }else{
                        failed.invoke("Doesn't exist.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    failed.invoke(error.message)
                }

            })
    }

    override fun getMyOrderList(
        path: String, //current, old
        success: (orderInfoList: List<OrderInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        val myOrderList = mutableListOf<OrderInfo>()
        val ref = realtime.getReference("orderInfo/shop").child(shopAdminEmail).child(path)

        ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    val info = snapshot.getValue<OrderInfo>()!!
                    if(info.isFoodHandover2User && info.foodHandover2UserTime != 0L && path == "current"){
                        ref.child(info.orderId).removeValue()
                        realtime.getReference("orderInfo/shop")
                            .child(shopAdminEmail)
                            .child("old")
                            .child(info.orderId)
                            .setValue(info)
                    }
                    else {
                        myOrderList.add(0,info)
                        success.invoke(myOrderList)
                    }
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val info = snapshot.getValue<OrderInfo>()!!
                    myOrderList.forEachIndexed { index, orderInfo ->
                        if(orderInfo.orderId == previousChildName){
                            success.invoke(myOrderList.toMutableList().apply {
                                removeAt(index)
                                add(index,info)
                            })
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val info = snapshot.getValue<OrderInfo>()!!
                    success.invoke(myOrderList.toMutableList().apply { remove(info) })
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    failed.invoke(error.message)
                }
            })
    }

    override fun getOrderInfo(
        orderId: String,
        path: String, //current, old
        success: (orderInfo: OrderInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        realtime
            .getReference("orderInfo/shop").child(shopAdminEmail).child(path).child(orderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val info = snapshot.getValue<OrderInfo>()
                    success.invoke(info ?: OrderInfo())
                }

                override fun onCancelled(error: DatabaseError) {
                    failed.invoke(error.message)
                }
            })
    }
    override fun updateOrderInfo(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        userEmail: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val shopAdminEmail: String = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        realtime
            .getReference("orderInfo/all")
            .child(userEmail)
            .child(orderId)
            .child(varBoolName)
            .setValue(value)

        realtime
            .getReference("orderInfo/current")
            .child(orderId)
            .child(varBoolName)
            .setValue(value)
            .addOnSuccessListener {
                success.invoke()
            }
            .addOnFailureListener {
                failed.invoke(it.message.toString())
            }
        realtime
            .getReference("orderInfo/shop")
            .child(shopAdminEmail)
            .child("current")
            .child(orderId)
            .child(varBoolName)
            .setValue(value)

        val time = System.currentTimeMillis()
        realtime
            .getReference("orderInfo/all")
            .child(userEmail)
            .child(orderId)
            .child(varTimeName)
            .setValue(time)
        realtime
            .getReference("orderInfo/current")
            .child(orderId)
            .child(varTimeName)
            .setValue(time)
        realtime
            .getReference("orderInfo/shop")
            .child(shopAdminEmail)
            .child("current")
            .child(orderId)
            .child(varTimeName)
            .setValue(time)
    }
}