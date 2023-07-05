package com.diu.mlab.foodie.admin.presentation.main.seller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.admin.databinding.ItemProcessInfoBinding

class ProgressListViewAdapter(
    private val progressList: List<Pair<String, String>>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemProcessInfoBinding,
        private val contest: Context,
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            list: List<Pair<String, String>>,
            position: Int
        ) {
            binding.type.text = list[position].first
            binding.time.text = list[position].second

            if(position==0)
                binding.line.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ItemProcessInfoBinding.inflate( LayoutInflater.from(viewGroup.context), viewGroup, false ),
            viewGroup.context
        )

    override fun getItemCount() = progressList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindView(progressList, position)
    }
}
