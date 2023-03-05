package com.diu.mlab.foodie.admin.presentation.main.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.databinding.FragmentRequestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestFragment : Fragment() {
    private var type: String? = null
    private val viewModel : AdminMainViewModel by viewModels()
    private lateinit var binding: FragmentRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { type = it.getString("type") }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        viewModel.superUserList.observe(requireActivity()){

        }
        viewModel.getSuperUserList(type!!){
            Log.d("TAG", "onCreateView: $it")
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(type: String) =
            RequestFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
    }
}