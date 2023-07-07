package com.diu.mlab.foodie.admin.presentation.main.seller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diu.mlab.foodie.admin.databinding.ActivityOrderInfoBinding
import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener
import com.diu.mlab.foodie.admin.util.toDateTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderInfoBinding
    private val viewModel: SellerMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        val orderId = bundle?.getString("orderId") ?: " "
        val path = bundle?.getString("path") ?: " "
        var orderInfo = OrderInfo()

        viewModel.getOrderInfo(orderId,path){
            MainScope().launch {
                Toast.makeText(this@OrderInfoActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.orderInfo.observe(this){ info ->
            orderInfo = info
            binding.foodNm.text = info.foodInfo.nm
            if(info.foodInfo.types.isNotEmpty()){
                binding.foodType.text = info.type
                binding.typeLayout.visibility = View.VISIBLE
            }
            binding.quantity.text = info.quantity.toString()
            binding.foodPrice.text = "${info.typePrice*info.quantity}"
            binding.totalPrice.text = "${info.typePrice*info.quantity+info.deliveryCharge}"
            binding.deliveryCharge.text = "${info.deliveryCharge}"
            binding.deliveryTime.text = info.foodInfo.time
            binding.orderTime.text = info.orderTime.toDateTime()

            binding.cusNm.text = info.userInfo.nm
            binding.cusDes.text = info.userInfo.userType
            binding.cusPn.text = info.userInfo.phone
            info.userInfo.pic.getDrawable { binding.cusPic.setImageDrawable(it) }
            binding.cusPn.setBounceClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL )
                callIntent.data = Uri.parse("tel:" + info.userInfo.phone)
                startActivity(callIntent)
            }
            if(info.runnerInfo.nm.isNotEmpty()){
                info.runnerInfo.pic.getDrawable { binding.runPic.setImageDrawable(it) }
                binding.runNm.text = info.runnerInfo.nm
                binding.runDes.text = "Runner"
                binding.runPn.text = info.runnerInfo.phone
                binding.runnerCard.visibility = View.VISIBLE
            }
            binding.runPn.setBounceClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL )
                callIntent.data = Uri.parse("tel:" + info.runnerInfo.phone)
                startActivity(callIntent)
            }

            if(
                (info.isPaid && info.paymentConfirmationTime == 0L) ||
                (info.isRunnerChosen && !info.isFoodHandover2RunnerNdPaid)
            )
                binding.foodConfirmation.visibility = View.VISIBLE
            else
                binding.foodConfirmation.visibility = View.GONE


            if(info.isRunnerChosen && info.isPaymentConfirmed){
                binding.confirmationText.text = "Did you handover the food to Runner and paid the delivery amount?"
                binding.btnNo.visibility = View.GONE
                binding.btnYes.text= "Mark As Done"
            }
        }

        viewModel.progressInfoList.observe(this){lst->
            binding.precessRecyclerView.adapter = ProgressListViewAdapter(lst)

            if( lst.map{it.first}.contains("Canceled") || lst.map{it.first}.contains("Food Received"))
                binding.processingBar.visibility = View.INVISIBLE
        }

        binding.btnEdit.setBounceClickListener {
            when(binding.foodConfirmation.visibility){
                View.GONE -> binding.foodConfirmation.visibility = View.VISIBLE
                View.VISIBLE -> binding.foodConfirmation.visibility = View.GONE
            }
        }

        binding.btnYes.setBounceClickListener {
            if(!orderInfo.isRunnerChosen) {
                viewModel.updateOrderInfo(
                    orderId = orderId,
                    varBoolName = "paymentConfirmed",
                    value = true,
                    varTimeName = "paymentConfirmationTime",
                    userEmail = orderInfo.userInfo.email,
                    success = {},
                    failed = {}
                )
            }else{
                viewModel.updateOrderInfo(
                    orderId = orderId,
                    varBoolName = "foodHandover2RunnerNdPaid",
                    value = true,
                    varTimeName = "foodHandover2RunnerTime",
                    userEmail = orderInfo.userInfo.email,
                    success = {},
                    failed = {}
                )
            }
            binding.foodConfirmation.visibility = View.GONE
        }

        binding.btnNo.setBounceClickListener {
            if(!orderInfo.isRunnerChosen) {
                viewModel.updateOrderInfo(
                    orderId = orderId,
                    varBoolName = "paymentConfirmed",
                    value = false,
                    varTimeName = "paymentConfirmationTime",
                    userEmail = orderInfo.userInfo.email,
                    success = {},
                    failed = {}
                )
            }
            binding.foodConfirmation.visibility = View.GONE
        }


        binding.btnBack.setBounceClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }


}