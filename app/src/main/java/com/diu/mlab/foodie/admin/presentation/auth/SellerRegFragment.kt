package com.diu.mlab.foodie.admin.presentation.auth

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.databinding.FragmentSellerRegBinding
import com.diu.mlab.foodie.admin.domain.RequestState
import com.diu.mlab.foodie.admin.domain.model.ShopInfo
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.presentation.main.seller.SellerMainViewModel
import com.diu.mlab.foodie.admin.util.copyImageToUriString
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


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
    private var qrUri : String?= null

    private var tmpShop = ShopInfo()
    private var logoUpdated: Boolean = false
    private var coverUpdated: Boolean = false
    private var qrUpdated: Boolean = false

    private var galleryLauncher4logo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            logoUri = uri
            binding.logo.setImageURI(logoUri)
            Log.d("TAG", "${logoUri?.path}")
            logoUpdated = true
        }
    }

    private var galleryLauncher4cover = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            coverUri = uri
            binding.cover.setImageURI(coverUri)
            Log.d("TAG", "$coverUri")
            coverUpdated = true
        }
    }

    private var barLauncher = registerForActivityResult(ScanContract()) { result ->
        Log.d("TAG qr", result?.contents ?: "")
        if(result != null && result.formatName=="QR_CODE"){
            val qrgEncoder = QRGEncoder(result.contents, null, QRGContents.Type.TEXT, 512)
            qrgEncoder.colorBlack = Color.WHITE
            qrgEncoder.colorWhite = Color.BLACK
            try {
                // Getting QR-Code as Bitmap
                val bitmap = qrgEncoder.bitmap
                // Setting Bitmap to ImageView
                binding.qr.setImageBitmap(bitmap)
                binding.qrTxt.text = "[QR Code Found]"
                qrUri = requireContext().copyImageToUriString(bitmap)
//                qrUri?.getDrawable { binding.qr.setImageDrawable(it) }
                qrUpdated = true
            } catch (e: WriterException) {
                Log.v("TAG", e.toString())
            }
        }else{
            Toast.makeText(requireContext(), "QR code not found", Toast.LENGTH_SHORT).show()
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
            sellerViewModel.getShopProfile{
                Log.d("TAG", "onCreateView: $it")
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerRegBinding.inflate(inflater, container, false)
        val photoPicker = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

        if(type=="server"){
            sellerViewModel.myShopProfile.observe(requireActivity()){shop ->
                tmpShop = shop
                binding.shopNm.setText(shop.nm)
                binding.loc.setText(shop.loc,false)
                binding.paymentType.setText(shop.paymentType,false)
                binding.pnNo.setText(shop.phone)
                shop.pic.getDrawable{ binding.logo.setImageDrawable(it) }
                shop.cover.getDrawable{ binding.cover.setImageDrawable(it) }
                if(shop.qr!="")
                    shop.qr.getDrawable{ binding.qr.setImageDrawable(it) }

                Log.d("TAG", "onCreate: myProfile observe $shop")
            }
            binding.btnRegister.text = "SAVE"
        }

        binding.logo.setOnClickListener { galleryLauncher4logo.launch(photoPicker) }
        binding.cover.setOnClickListener { galleryLauncher4cover.launch(photoPicker) }
        binding.btnRegister.setBounceClickListener{
            superUser = SuperUser(
                nm = binding.shopNm.text.toString(),
                email = "",
                phone = binding.pnNo.text.toString(),
                userType = "shop",
                paymentType = binding.paymentType.text.toString(),
                status = "pending",
                pic = logoUri?.toString() ?: "",
                cover = coverUri?.toString() ?: "",
                qr = qrUri?: "",
                loc = binding.loc.text.toString()
            )
            val tmp = tmpShop.copy(
                nm = binding.shopNm.text.toString(),
                phone = binding.pnNo.text.toString(),
                paymentType = binding.paymentType.text.toString(),
                pic = logoUri?.toString() ?: tmpShop.pic,
                cover = coverUri?.toString() ?: tmpShop.cover,
                qr = qrUri?: "",
                loc = binding.loc.text.toString()
            )
            if(type=="server"){
                sellerViewModel.updateShopProfile(tmp,logoUpdated, coverUpdated, qrUpdated,{
                    Log.e("TAG", "success")
                    MainScope().launch {
                        Toast.makeText(requireContext(), "Successfully saved", Toast.LENGTH_SHORT).show()
                    }
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                },{
                    Log.e("TAG", "failed: $it")
                    MainScope().launch {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else{
                viewModel.googleSignIn(requireActivity(), superUser) {result ->
                    when(result){
                        is RequestState.Error -> {
                            Log.e("TAG", "failed: ${result.error}")
                            if(result.code!= 20)
                                Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                        }
                        is RequestState.Success -> {
                            val credential = result.data
                            viewModel.firebaseSignup(credential,superUser.copy(email = credential.id.transformedEmailId()),
                                success = {
                                    val intent = Intent(requireContext(), PendingActivity::class.java)
                                    intent.putExtra("status", superUser.status)
                                    startActivity(intent)
                                    requireActivity().finish()
                                    Log.d("TAG", "success:")
                                }, failed = {
                                    Log.e("TAG", "failed: $it")
                                    MainScope().launch {
                                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    }
                }

            }

        }
        binding.qr.setBounceClickListener {
            val options = ScanOptions()
            options.setPrompt("")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            options.captureActivity = CaptureQR::class.java
            barLauncher.launch(options)
        }
        return binding.root
    }

}