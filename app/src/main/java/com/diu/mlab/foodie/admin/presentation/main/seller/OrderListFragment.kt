package com.diu.mlab.foodie.admin.presentation.main.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.databinding.FragmentOrderListBinding
import com.jem.fliptabs.FlipTab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderListFragment : Fragment() {

    private val viewModel: SellerMainViewModel by viewModels()
    private lateinit var binding: FragmentOrderListBinding
    private var path = "current"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderListBinding.inflate(inflater, container, false)
        viewModel.getMyOrderList("current") {
            MainScope().launch{
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.orderList.observe(viewLifecycleOwner){
            binding.foodListRecyclerView.adapter = OrderListViewAdapter(it,path)
        }

        binding.flipTab.setTabSelectedListener(object: FlipTab.TabSelectedListener {
            override fun onTabSelected(isLeftTab: Boolean, tabTextValue: String) {
                if (isLeftTab){
                    path = "current"
                    viewModel.getMyOrderList(path) {
                        MainScope().launch{
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    path = "old"
                    viewModel.getMyOrderList(path) {
                        MainScope().launch{
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onTabReselected(isLeftTab: Boolean, tabTextValue: String) {}
        })
        return binding.root
    }
}