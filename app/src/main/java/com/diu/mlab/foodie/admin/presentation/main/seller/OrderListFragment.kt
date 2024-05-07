package com.diu.mlab.foodie.admin.presentation.main.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.admin.databinding.FragmentOrderListBinding
import com.jem.fliptabs.FlipTab
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OrderListFragment : Fragment() {

    private val viewModel: SellerMainViewModel by activityViewModels()
    private lateinit var binding: FragmentOrderListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderListBinding.inflate(inflater, container, false)
        viewModel.getMyOrderList(viewModel.path) {
            MainScope().launch{
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.orderList.observe(viewLifecycleOwner){
            binding.foodListRecyclerView.adapter = OrderListViewAdapter(it,viewModel.path)
        }

        binding.flipTab.setTabSelectedListener(object: FlipTab.TabSelectedListener {
            override fun onTabSelected(isLeftTab: Boolean, tabTextValue: String) {
                viewModel.path = if (isLeftTab) "current" else  "old"
                viewModel.getMyOrderList(viewModel.path) {
                    MainScope().launch{
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onTabReselected(isLeftTab: Boolean, tabTextValue: String) {}
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when (viewModel.path) {
            "current" -> binding.flipTab.selectLeftTab(false)
            "old" -> binding.flipTab.selectRightTab(false)
        }
    }
}