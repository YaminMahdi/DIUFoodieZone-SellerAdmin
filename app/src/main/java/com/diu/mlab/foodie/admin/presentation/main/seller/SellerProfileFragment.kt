package com.diu.mlab.foodie.admin.presentation.main.seller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentSellerProfileBinding
import com.diu.mlab.foodie.admin.presentation.auth.LoginActivity
import com.diu.mlab.foodie.admin.presentation.auth.SellerRegFragment
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

class SellerProfileFragment : Fragment() {
    private val viewModel: SellerMainViewModel by activityViewModels()
    private lateinit var emailId : String
    private lateinit var binding: FragmentSellerProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerProfileBinding.inflate(inflater,container,false)
        emailId = Firebase.auth.currentUser!!.email!!.transformedEmailId()
        viewModel.getShopProfile{
            Log.d("TAG", "getShopProfile failed: $it")
        }

        viewModel.myShopProfile.observe(requireActivity()){shop ->
            binding.shopView.nm.text = shop.nm
            binding.shopView.pn.text = shop.phone
            binding.shopView.loc.text = shop.loc
            shop.pic.getDrawable{ binding.shopView.pic.setImageDrawable(it) }
            shop.cover.getDrawable{ binding.shopView.cover.setImageDrawable(it) }
            Log.d("TAG", "onCreate: myProfile observe $shop")
        }

        binding.shopView.accept.visibility = View.GONE
        binding.shopView.reject.visibility = View.GONE
        binding.edit.setBounceClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.run {
                addToBackStack("xyz")
                hide(this@SellerProfileFragment)
                add(R.id.sellFragment, SellerRegFragment.newInstance("server"))
                commit()
            }

        }
        binding.logout.setBounceClickListener{
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            activity?.finish()
        }


        return binding.root
    }

}