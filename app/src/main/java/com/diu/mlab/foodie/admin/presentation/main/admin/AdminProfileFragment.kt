package com.diu.mlab.foodie.admin.presentation.main.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentAdminProfileBinding
import com.diu.mlab.foodie.admin.presentation.auth.AdminRegFragment
import com.diu.mlab.foodie.admin.presentation.auth.LoginActivity
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AdminProfileFragment : Fragment() {
    private val viewModel : AdminMainViewModel by activityViewModels()
    private lateinit var emailId : String
    private lateinit var binding: FragmentAdminProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        emailId = Firebase.auth.currentUser?.email?.transformedEmailId() ?: "nai"
        requireActivity().supportFragmentManager.addOnBackStackChangedListener {
            viewModel.getMyProfile(emailId){
                Log.e("TAG", "getMyProfile failed: $it")
            }
        }
        viewModel.getMyProfile(emailId){
            Log.d("TAG", "onCreate getMyProfile: $it")
        }
        viewModel.myProfile.observe(requireActivity()){ user->
            binding.nm.text = user.nm
            binding.pn.text = user.phone
            binding.des.text = user.loc

            user.pic.getDrawable{ binding.pic.setImageDrawable(it) }
            Log.d("TAG", "onCreate: myProfile observe $user")
        }

        binding.edit.setBounceClickListener{
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.run {
                addToBackStack("xyz")
                hide(this@AdminProfileFragment)
                add(R.id.requestFragment, AdminRegFragment.newInstance("server"))
                commit()
            }

        }
        binding.logout.setBounceClickListener{
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
        return binding.root
    }

}