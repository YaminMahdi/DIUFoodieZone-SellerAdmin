package com.diu.mlab.foodie.admin.presentation.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.diu.mlab.foodie.admin.databinding.FragmentSellerRegBinding
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.presentation.main.PendingActivity
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerRegFragment : Fragment() {

    companion object {
        fun newInstance() = SellerRegFragment()
    }

    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: FragmentSellerRegBinding
    private lateinit var superUser: SuperUser
    private var logoUri : Uri?= null
    private var coverUri : Uri?= null

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val credential: SignInCredential = Identity.getSignInClient(requireActivity()).getSignInCredentialFromIntent(data)
            viewModel.firebaseSignup(credential,superUser.copy(email = credential.id.transformedEmailId()),{
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

        }
    }

    private var galleryLauncher4cover = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coverUri = result.data?.data
            binding.cover.setImageURI(coverUri)
            Log.d("TAG", "$coverUri")
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        binding = FragmentSellerRegBinding.inflate(inflater, container, false)
        binding.logo.setOnClickListener { galleryLauncher4logo.launch(gallery) }
        binding.cover.setOnClickListener { galleryLauncher4cover.launch(gallery) }
        binding.btnRegister.setBounceClickListener{
            superUser = SuperUser(
                nm = binding.shopNm.text.toString(),
                email = "",
                phone = binding.pnNo.text.toString(),
                userType = "shop",
                status = "pending",
                pic = logoUri.toString(),
                cover = coverUri.toString(),
                loc = binding.loc.text.toString()
            )
            viewModel.googleSignIn(requireActivity(),resultLauncher){
                Log.e("TAG", "failed: $it")
                Looper.prepare()
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Looper.loop()

            }
        }
        return binding.root
    }
}