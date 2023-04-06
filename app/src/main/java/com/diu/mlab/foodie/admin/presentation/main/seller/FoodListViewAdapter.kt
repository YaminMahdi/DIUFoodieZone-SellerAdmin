package com.diu.mlab.foodie.admin.presentation.main.seller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.admin.databinding.ItemAdmin2Binding
import com.diu.mlab.foodie.admin.databinding.ItemAdminBinding
import com.diu.mlab.foodie.admin.databinding.ItemFoodBinding
import com.diu.mlab.foodie.admin.databinding.ItemShopBinding
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.presentation.main.admin.AdminMainActivity
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener

class FoodListViewAdapter(
    private val foodList: List<FoodItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemFoodBinding,
        private val contest: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<FoodItem>, position: Int) {
            binding.nm.text = list[position].nm
            binding.taha.text = list[position].price
            binding.time.text = list[position].time
            list[position].pic.getDrawable{ binding.pic.setImageDrawable(it) }
            binding.root.setBounceClickListener {
               // contest.startActivity(Intent(contest, AdminMainActivity::class.java).putExtra("foodKey",list[position].key))
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemFoodBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context
        )

    override fun getItemCount() = foodList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindView(foodList, position)
    }
}
