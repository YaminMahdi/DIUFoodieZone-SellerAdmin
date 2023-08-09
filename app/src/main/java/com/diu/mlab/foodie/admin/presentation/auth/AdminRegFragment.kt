package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentAdminRegBinding
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.presentation.main.admin.AdminMainViewModel
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminRegFragment : Fragment() {
    private var type: String = "local"

    companion object {
        fun newInstance(type: String) =
            AdminRegFragment().apply {
                arguments = bundleOf("type" to type)
            }
    }
    private val viewModel : AuthViewModel by viewModels()
    private val adminViewModel : AdminMainViewModel by viewModels()
    private lateinit var binding: FragmentAdminRegBinding
    private lateinit var superUser: SuperUser
    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor
    private var picUpdated: Boolean = false
    private var picUri : Uri?= null
    private var tmpProfile = SuperUser()

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
                    MainScope().launch {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.d("TAG", "RESULT_CANCELED")

        }
    }

    private var galleryLauncher4Pic = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            picUri = result.data?.data
            binding.pic.setImageURI(picUri)
            Log.d("TAG", "${picUri?.path}")
            picUpdated = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type")!!
            Log.d("TAG", "onCreate: $type")
        }
        Log.d("TAG", "onCreate: $type")
        if(type=="server"){
            adminViewModel.getMyProfile(Firebase.auth.currentUser!!.email!!.transformedEmailId()){
                Log.d("TAG", "onCreateView: $it")
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        preferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        preferencesEditor = preferences.edit()
        binding = FragmentAdminRegBinding.inflate(inflater, container, false)
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        if(type=="server"){
            adminViewModel.myProfile.observe(requireActivity()){profile ->
                tmpProfile = profile
                binding.adminNm.setText(profile.nm)
                binding.work.setText(profile.loc,false)
                binding.pnNo.setText(profile.phone)
                profile.pic.getDrawable{ binding.pic.setImageDrawable(it) }
                Log.d("TAG", "onCreate: myProfile observe $profile")
            }
            binding.btnRegister.text = "SAVE"
        }
        else{
            binding.picCard.visibility = View.GONE
        }
        binding.pic.setOnClickListener { galleryLauncher4Pic.launch(gallery) }
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
            val updatedUser = tmpProfile.copy(
                nm = binding.adminNm.text.toString(),
                phone = binding.pnNo.text.toString(),
                pic = picUri?.toString() ?: tmpProfile.pic,
                loc = binding.work.text.toString()
            )
            if(type=="server"){
                adminViewModel.updateAdminProfile(updatedUser,picUpdated,{
                    Log.e("TAG", "success")
                    MainScope().launch {
                        Toast.makeText(requireContext(), "Successfully saved", Toast.LENGTH_SHORT).show()
                    }
                    requireActivity().onBackPressedDispatcher.onBackPressed()
               },{
                    Log.e("TAG", "updateAdminProfile failed: $it")
                    MainScope().launch {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
               })
            }
            else{
                viewModel.googleSignIn(requireActivity(), resultLauncher, superUser) {
                    Log.e("TAG", "googleSignIn failed: $it")
                    MainScope().launch {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

}