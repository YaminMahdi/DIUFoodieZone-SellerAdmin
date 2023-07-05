package com.diu.mlab.foodie.admin.presentation.main.seller

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentFoodListBinding
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodListFragment : Fragment() {

    private val viewModel: SellerMainViewModel by viewModels()
    private lateinit var preferences: SharedPreferences
    private lateinit var emailId : String
    private lateinit var binding: FragmentFoodListBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFoodListBinding.inflate(inflater, container, false)

        preferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), AppCompatActivity.MODE_PRIVATE)

        emailId = preferences.getString("email", "nai")!!

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