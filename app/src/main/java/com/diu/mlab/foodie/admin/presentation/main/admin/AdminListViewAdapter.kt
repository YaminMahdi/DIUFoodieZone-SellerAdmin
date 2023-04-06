package com.diu.mlab.foodie.admin.presentation.main.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.admin.databinding.ItemAdmin2Binding
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener


class AdminListViewAdapter(
    private val classList: List<SuperUser>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolderAdmin(
        private val binding: ItemAdmin2Binding,
        private val contest: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<SuperUser>, position: Int) {
            binding.nm.text = list[position].nm
            binding.pn.text = list[position].phone
            binding.des.text = list[position].loc
            list[position].pic.getDrawable{ binding.pic.setImageDrawable(it) }
            binding.pn.setBounceClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL )
                callIntent.data = Uri.parse("tel:" + list[position].phone) //change the number
                contest.startActivity(callIntent)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolderAdmin(
            ItemAdmin2Binding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context
        )

    override fun getItemCount() = classList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderAdmin).bindView(classList, position)
    }
}
