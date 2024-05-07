package com.diu.mlab.foodie.admin.presentation.main.seller

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.admin.databinding.FragmentFoodListBinding
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

class FoodListFragment : Fragment() {

    private val viewModel: SellerMainViewModel by activityViewModels()
    private lateinit var emailId : String
    private lateinit var binding: FragmentFoodListBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFoodListBinding.inflate(inflater, container, false)


        emailId = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"

        viewModel.getFoodList{
            Log.d("TAG", "onCreate getFoodList: $it")
        }
        val manager = requireActivity().supportFragmentManager
        val foodList = mutableListOf<FoodItem>()
        val adapter = FoodListViewAdapter(foodList,manager,this@FoodListFragment)
        binding.foodListRecyclerView.adapter = adapter

        viewModel.foodList.observe(requireActivity()){ foodLst->
            foodList.clear()
            foodList.addAll(foodLst)
            Log.d("TAG", "observe: new food")
            adapter.notifyDataSetChanged()
            Log.d("TAG", "onCreate: myProfile observe $foodList")
        }

        return binding.root
    }

}