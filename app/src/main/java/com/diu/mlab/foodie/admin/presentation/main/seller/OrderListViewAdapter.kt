package com.diu.mlab.foodie.admin.presentation.main.seller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.ItemOrderBinding
import com.diu.mlab.foodie.admin.domain.model.OrderInfo
import com.diu.mlab.foodie.admin.util.loadDrawable

class OrderListViewAdapter(
    private val progressList: List<OrderInfo>,
    private val path: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemOrderBinding,
        private val contest: Context,
        private val path: String
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindView(
            list: List<OrderInfo>,
            position: Int
        ) {
            binding.nm.text = "${list[position].foodInfo.nm} ${list[position].type}"
            binding.userNm.text = list[position].userInfo.nm.split("(?<=\\D)(?=\\d)".toRegex())[0]
            binding.userType.text = list[position].userInfo.userType

            binding.price.text = (list[position].typePrice * list[position].quantity + list[position].deliveryCharge).toString()
            binding.quantity.text = list[position].quantity.toString()
            binding.pic.loadDrawable(list[position].foodInfo.pic)


            if(list[position].isUserReceived)
                binding.orderStatusTxt.text = "Received"
            else if(!list[position].isUserReceived && list[position].userReceivedTime != 0L)
                binding.orderStatusTxt.text = "Not Received"
            else if(list[position].isFoodHandover2User)
                binding.orderStatusTxt.text = "Delivered"
            else if(list[position].isFoodHandover2RunnerNdPaid)
                binding.orderStatusTxt.text = "On The Way"
            else if(list[position].isCanceled)
                binding.orderStatusTxt.text = "Canceled"
            else if(list[position].isPaymentConfirmed)
                binding.orderStatusTxt.text = "Payment Successful"
            else if(!list[position].isPaymentConfirmed && list[position].paymentConfirmationTime != 0L)
                binding.orderStatusTxt.text = "Payment Failed"
            else if(list[position].isPaid)
                binding.orderStatusTxt.text = "Processing"
            else if(!list[position].isPaid && list[position].paymentTime != 0L)
                binding.orderStatusTxt.text = "Not Paid"
            else
                binding.orderStatusTxt.text = "Processing"



            if((!list[position].isPaymentConfirmed && list[position].paymentConfirmationTime != 0L) || list[position].isCanceled)
                binding.orderStatusCard.backgroundTintList = ColorStateList.valueOf(contest.getColor(R.color.redZ))
            if(list[position].isFoodHandover2User || list[position].isUserReceived)
                binding.orderStatusCard.backgroundTintList = ColorStateList.valueOf(contest.getColor(R.color.greenX))


            binding.btnViewOrder.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("orderId", list[position].orderId)
                bundle.putString("path", path)

                contest.startActivity(
                    Intent(contest, OrderInfoActivity::class.java).putExtras(bundle)
                )
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context, path
        )

    override fun getItemCount() = progressList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindView(progressList, position)
    }
}
