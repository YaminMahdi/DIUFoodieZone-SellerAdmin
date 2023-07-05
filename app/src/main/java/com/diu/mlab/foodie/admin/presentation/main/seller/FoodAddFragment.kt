package com.diu.mlab.foodie.admin.presentation.main.seller

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentFoodAddBinding
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.util.addLiveTextListener
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodAddFragment : Fragment() {
    private var foodId: String? = null
    companion object {
        fun newInstance(foodId: String) =
            FoodAddFragment().apply {
                arguments = bundleOf("foodId" to foodId)
            }
    }
    private val viewModel: SellerMainViewModel by viewModels()
    private lateinit var emailId : String
    private lateinit var binding: FragmentFoodAddBinding
    private var nm : String= ""
    private var picUpdated: Boolean = false
    private var picUri : Uri?= null
    private var price : String= ""
    private var time : String= ""
    private var food = FoodItem()


    private var galleryLauncher4FoodPic = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            picUri = uri
            binding.foodView.pic.setImageURI(picUri)
            Log.d("TAG", "${picUri?.path}")
            picUpdated = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            foodId = it.getString("foodId")!!
            Log.d("TAG", "onCreate: $foodId")
        }
        if(!foodId.isNullOrEmpty()){
            viewModel.getFoodInfo(foodId!!){
                Log.d("TAG", "onCreateView: $it")
            }
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodAddBinding.inflate(inflater, container, false)
        emailId = Firebase.auth.currentUser!!.email!!.transformedEmailId()
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val photoPicker = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

        if(!foodId.isNullOrEmpty()){
            viewModel.foodInfo.observe(requireActivity()){fud ->
                food=fud
                binding.foodNm.setText(fud.nm)
                binding.foodTaha.setText(fud.price)
                binding.foodTime.setText(fud.time,false)
                binding.foodAvailability.setText(fud.status,false)

                binding.foodView.nm.text = fud.nm
                binding.foodView.taha.text = fud.price.split(",")[0]
                binding.foodView.time.text = fud.time
                fud.pic.getDrawable{ binding.foodView.pic.setImageDrawable(it) }

                if(fud.types.isNotEmpty()) {
                    binding.foodType.setText(fud.types)
                    binding.typeLayout.visibility = View.VISIBLE
                    binding.typeSwitch.isChecked = true
                }

                Log.d("TAG", "onCreate: myProfile observe $fud")
                binding.btnAddFood.text = "SAVE"
            }
        }

        binding.typeSwitch.setOnCheckedChangeListener { buttonView, isChecked->
            if(isChecked){
                binding.typeLayout.visibility = View.VISIBLE
                binding.foodTahaLayout.hint = "Type Prices"
                binding.foodTahaLayout.placeholderText = "10,20,30"
                binding.foodTaha.inputType = InputType.TYPE_CLASS_TEXT
                binding.foodTahaLayout.placeholderTextColor = ColorStateList.valueOf(requireContext().getColor(R.color.darkY))

            }
            else{
                binding.typeLayout.visibility = View.GONE
                binding.foodTahaLayout.hint = "Price"
                binding.foodTahaLayout.placeholderText = ""
                binding.foodTaha.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        binding.foodView.pic.setImageResource(R.drawable.cam1)
        binding.foodView.pic.setOnClickListener{ galleryLauncher4FoodPic.launch(photoPicker) }
        binding.foodNm.addLiveTextListener{
            nm = it
            Log.d("TAG", "addLiveTextListener: $it")
            if(it.isNotEmpty())
                binding.foodView.nm.text = it
            else
                binding.foodView.nm.text = "Food Name"
        }
        binding.foodTaha.addLiveTextListener{
            price = it
            Log.d("TAG", "addLiveTextListener: $it")
            if(it.isNotEmpty())
                binding.foodView.taha.text = it
            else
                binding.foodView.taha.text = "Price"

        }
        binding.foodTime.addLiveTextListener{
            time = it
            Log.d("TAG", "addLiveTextListener: $it")
            if(it.isNotEmpty())
                binding.foodView.time.text = it
            else
                binding.foodView.time.text = "Time"
        }

        binding.btnAddFood.setBounceClickListener {
            val newFood =
                FoodItem(
                    foodId = "",
                    nm = nm,
                    pic = picUri?.toString() ?: "",
                    price = price,
                    time = time,
                    status = binding.foodAvailability.text.toString(),
                    types = binding.foodType.text.toString()
                )
            val updatedFood =
                food.copy(
                    nm = nm,
                    pic = picUri?.toString() ?: food.pic,
                    price = price,
                    time = time,
                    status = binding.foodAvailability.text.toString(),
                    types = binding.foodType.text.toString()
                )
            if(!foodId.isNullOrEmpty()){
                viewModel.updateFood(updatedFood,{
                    MainScope().launch {
                        Toast.makeText(requireContext(), "Food Updated", Toast.LENGTH_SHORT).show()
                    }
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                },{
                    MainScope().launch {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                viewModel.addFood(newFood,{
                    binding.foodNm.setText("")
                    binding.foodTaha.setText("")
                    binding.foodTime.setText("")
                    binding.foodAvailability.setText("")

                    binding.foodView.nm.text = "Food Name"
                    binding.foodView.taha.text = "Price"
                    binding.foodView.time.text = "Time"
                    binding.foodView.pic.setImageResource(R.drawable.cam1)
                    picUri = null

                    Log.d("TAG", "success")
                    MainScope().launch {
                        Toast.makeText(requireContext(), "Food Added", Toast.LENGTH_SHORT).show()
                    }
                }){
                    Log.d("TAG", "addFood: $it")
                    MainScope().launch {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        return binding.root
    }

}