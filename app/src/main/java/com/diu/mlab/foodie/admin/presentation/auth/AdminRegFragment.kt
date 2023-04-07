package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentAdminRegBinding
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminRegFragment : Fragment() {

    companion object {
        fun newInstance() = AdminRegFragment()
    }
    private val viewModel : AuthViewModel by viewModels()
//    private lateinit var viewModel : AuthViewModel
    private lateinit var binding: FragmentAdminRegBinding
    private lateinit var superUser: SuperUser
    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(requireActivity()).getSignInCredentialFromIntent(data)
            val pic = credential.profilePictureUri.toString().replace("=s96","=s384")
            viewModel.firebaseSignup(credential,
                superUser.copy(email = credential.id.transformedEmailId(), pic = pic),{
                    preferencesEditor.putString("email", credential.id.transformedEmailId()).apply()
                    val intent = Intent(requireContext(), PendingActivity::class.java)
                    intent.putExtra("status", superUser.status)
                    startActivity(intent)
                    Log.d("TAG", "success:")
                }){
                    Log.e("TAG", "failed: $it")
                    Looper.prepare()
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.d("TAG", "RESULT_CANCELED")

        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        preferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        preferencesEditor = preferences.edit()
        binding = FragmentAdminRegBinding.inflate(inflater, container, false)
        binding.btnRegister.setBounceClickListener{
            superUser = SuperUser(
                nm = binding.adminNm.text.toString(),
                email = "",
                phone = binding.pnNo.text.toString(),
                userType = "admin",
                status = "pending",
                pic = "",
                cover = "",
                loc = binding.work.text.toString()
            )
            viewModel.googleSignIn(requireActivity(),resultLauncher, superUser){
                Log.e("TAG", "failed: $it")
                Looper.prepare()
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
        return binding.root
    }

}