package com.diu.mlab.foodie.admin.presentation.main.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.databinding.FragmentRequestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestFragment : Fragment() {
    private var type: String = "pending"
    private val viewModel : AdminMainViewModel by viewModels()
    private lateinit var binding: FragmentRequestBinding

    companion object {
        fun newInstance(type: String) =
            RequestFragment().apply {
                arguments = bundleOf("type" to type)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type")!!
            Log.d("TAG", "onCreate: $type")
        }
        Log.d("TAG", "onCreate: $type")
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        viewModel.superUserList.observe(requireActivity()){
            val adapter = RecyclerViewAdapter(it,viewModel,type)
            binding.recyclerView.adapter = adapter
            //adapter.notifyDataSetChanged()
            Log.d("TAG", "onCreateView: observe ${it.count()}")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSuperUserList(type){
            Log.d("TAG", "onCreateView: $it")
        }
    }

}