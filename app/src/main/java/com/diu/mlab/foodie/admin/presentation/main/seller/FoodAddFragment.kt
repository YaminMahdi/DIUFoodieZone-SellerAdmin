package com.diu.mlab.foodie.admin.presentation.main.seller

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.FragmentFoodAddBinding
import com.diu.mlab.foodie.admin.domain.model.FoodItem
import com.diu.mlab.foodie.admin.util.addLiveTextListener
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodAddFragment : Fragment() {

    private val viewModel: SellerMainViewModel by viewModels()
    private lateinit var emailId : String
    private lateinit var binding: FragmentFoodAddBinding
    private var nm : String= ""
    private var picUri : Uri?= null
    private var price : String= ""
    private var time : String= ""

    private var galleryLauncher4FoodPic = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            picUri = result.data?.data
            binding.foodView.pic.setImageURI(picUri)
            Log.d("TAG", "${picUri?.path}")

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

        binding.foodView.pic.setImageResource(R.drawable.cam1)
        binding.foodView.pic.setOnClickListener{ galleryLauncher4FoodPic.launch(gallery) }
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
            val food =
                FoodItem(
                    key = "",
                    nm = nm,
                    pic = picUri?.toString() ?: "",
                    price = price,
                    time = time,
                    status = binding.foodAvailability.text.toString(),
                )
            viewModel.addFood(food,emailId,{
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
                Looper.prepare()
                Toast.makeText(requireContext(), "Food Added", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }){
                Log.d("TAG", "addFood: $it")
                Looper.prepare()
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
        return binding.root
    }

}