package com.diu.mlab.foodie.admin.presentation.main.seller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.ItemFoodBinding
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.util.getDrawable

class FoodListViewAdapter(
    private val foodList: List<FoodItem>,
    private val manager: FragmentManager,
    private val foodListFragment: FoodListFragment,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemFoodBinding,
        private val contest: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            list: List<FoodItem>,
            position: Int,
            manager: FragmentManager,
            foodListFragment: FoodListFragment
        ) {
            if(list[position].status=="Unavailable")
                binding.root.alpha = .6f
            else
                binding.root.alpha = 1f
            binding.nm.text = list[position].nm
            binding.taha.text = list[position].price.split(",")[0]
            binding.time.text = list[position].time
            list[position].pic.getDrawable{ binding.pic.setImageDrawable(it) }
            binding.btnEditFood.setOnClickListener {
                manager
                    .beginTransaction()
                    .run {
                        addToBackStack("FoodListFragment")
                        hide(foodListFragment)
                        add(R.id.sellFragment, FoodAddFragment.newInstance(list[position].foodId))
                        commit()
                    }
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
        (holder as ViewHolder).bindView(foodList, position, manager, foodListFragment)
    }
}
