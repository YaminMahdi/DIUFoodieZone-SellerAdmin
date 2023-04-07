package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Filterable
import android.widget.ListAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentSellerRegBinding
import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.presentation.main.seller.SellerMainViewModel
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerRegFragment : Fragment() {
    private var type: String = "local"
    companion object {
        fun newInstance(type: String) =
            SellerRegFragment().apply {
                arguments = bundleOf("type" to type)
            }
    }

    private val viewModel : AuthViewModel by viewModels()
    private val sellerViewModel : SellerMainViewModel by viewModels()

    private lateinit var binding: FragmentSellerRegBinding
    private lateinit var superUser: SuperUser
    private var logoUri : Uri?= null
    private var coverUri : Uri?= null
    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor
    private var tmpShop = ShopInfo()
    private var logoUpdated: Boolean = false
    private var coverUpdated: Boolean = false

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(requireActivity()).getSignInCredentialFromIntent(data)
            viewModel.firebaseSignup(credential,superUser.copy(email = credential.id.transformedEmailId()),{
                preferencesEditor.putString("email", credential.id.transformedEmailId()).apply()
                val intent = Intent(requireContext(), PendingActivity::class.java)
                intent.putExtra("status", superUser.status)
                startActivity(intent)
                Log.d("TAG", "success:")
            }){
                Log.e("TAG", "failed: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.d("TAG", "RESULT_CANCELED")
        }
    }

    private var galleryLauncher4logo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            logoUri = result.data?.data
            binding.logo.setImageURI(logoUri)
            Log.d("TAG", "${logoUri?.path}")
            logoUpdated = true
        }
    }

    private var galleryLauncher4cover = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coverUri = result.data?.data
            binding.cover.setImageURI(coverUri)
            Log.d("TAG", "$coverUri")
            coverUpdated = true
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
            sellerViewModel.getShopProfile(Firebase.auth.currentUser!!.email!!.transformedEmailId()){
                Log.d("TAG", "onCreateView: $it")
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        preferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key), AppCompatActivity.MODE_PRIVATE
        )
        preferencesEditor = preferences.edit()
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        binding = FragmentSellerRegBinding.inflate(inflater, container, false)

        if(type=="server"){
            sellerViewModel.myShopProfile.observe(requireActivity()){shop ->
                tmpShop = shop
                binding.shopNm.setText(shop.nm)
                binding.loc.setText(shop.loc,false)
                binding.pnNo.setText(shop.phone)
                shop.pic.getDrawable{ binding.logo.setImageDrawable(it) }
                shop.cover.getDrawable{ binding.cover.setImageDrawable(it) }
                Log.d("TAG", "onCreate: myProfile observe $shop")
            }
            binding.btnRegister.text = "SAVE"
        }

        binding.logo.setOnClickListener { galleryLauncher4logo.launch(gallery) }
        binding.cover.setOnClickListener { galleryLauncher4cover.launch(gallery) }
        binding.btnRegister.setBounceClickListener{
            superUser = SuperUser(
                nm = binding.shopNm.text.toString(),
                email = "",
                phone = binding.pnNo.text.toString(),
                userType = "shop",
                status = "pending",
                pic = logoUri?.toString() ?: "",
                cover = coverUri?.toString() ?: "",
                loc = binding.loc.text.toString()
            )

            if(type=="server"){
                val tmp = tmpShop.copy(
                    nm = binding.shopNm.text.toString(),
                    phone = binding.pnNo.text.toString(),
                    pic = logoUri?.toString() ?: tmpShop.pic,
                    cover = coverUri?.toString() ?: tmpShop.cover,
                    loc = binding.loc.text.toString()
                )
                sellerViewModel.updateShopProfile(tmp,logoUpdated, coverUpdated,{
                    Log.e("TAG", "success")
                    Toast.makeText(requireContext(), "Successfully saved", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                },{
                    Log.e("TAG", "failed: $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                })
            }
            else{
                viewModel.googleSignIn(requireActivity(),resultLauncher, superUser){
                    Log.e("TAG", "failed: $it")
                    Looper.prepare()
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
            }

        }
        return binding.root
    }

}