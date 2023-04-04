package com.diu.mlab.foodie.admin.presentation.main.admin

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diu.mlab.foodie.admin.databinding.ItemAdminBinding
import com.diu.mlab.foodie.admin.databinding.ItemShopBinding
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.util.getDrawable
import com.diu.mlab.foodie.admin.util.setBounceClickListener

class RecyclerViewAdapter(
    private val classList: List<SuperUser>,
    private val viewModel: AdminMainViewModel,
    private val listType: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolderAdmin(
        private val binding: ItemAdminBinding,
        private val contest: Context,
        private val viewModel: AdminMainViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<SuperUser>, position: Int, listType: String) {
            binding.nm.text = list[position].nm
            binding.pn.text = list[position].phone
            binding.des.text = list[position].loc
            list[position].pic.getDrawable{ binding.pic.setImageDrawable(it) }
//            Picasso.get().load(list[position].pic).into(binding.pic)

            when(listType){
                "rejected"-> binding.reject.visibility = View.GONE
                "accepted"-> binding.accept.visibility = View.GONE
            }
            binding.accept.setBounceClickListener {
                viewModel.changeSuperUserList(list.toMutableList().apply { removeAt(position) } )
                viewModel.changeSuperUserStatus(list[position].copy(status = "accepted"),{
//                    contest.startActivity(Intent(contest, AdminMainActivity::class.java))
                },{
                    Log.d("TAG", "bindView: $it")
                })
            }
            binding.reject.setBounceClickListener {
                viewModel.changeSuperUserList(list.toMutableList().apply { removeAt(position) } )
                viewModel.changeSuperUserStatus(list[position].copy(status = "rejected"),{
//                    contest.startActivity(Intent(contest, AdminMainActivity::class.java))
                },{
                    Log.d("TAG", "bindView: $it")
                })
            }
        }
    }
    class ViewHolderShop(
        private val binding: ItemShopBinding,
        private val contest: Context,
        private val viewModel: AdminMainViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<SuperUser>, position: Int, listType: String) {
            binding.nm.text = list[position].nm
            binding.pn.text = list[position].phone
            binding.loc.text = list[position].loc
            list[position].pic.getDrawable{ binding.pic.setImageDrawable(it) }
            list[position].cover.getDrawable{ binding.cover.setImageDrawable(it) }

            when(listType){
                "rejected"-> binding.reject.visibility = View.GONE
                "accepted"-> binding.accept.visibility = View.GONE
            }
            binding.accept.setBounceClickListener {
                viewModel.changeSuperUserList(list.toMutableList().apply { removeAt(position) } )
                viewModel.changeSuperUserStatus(list[position].copy(status = "accepted"),{
                },{
                    Log.d("TAG", "bindView: $it")
                })
            }
            binding.reject.setBounceClickListener {
                viewModel.changeSuperUserList(list.toMutableList().apply { removeAt(position) } )
                viewModel.changeSuperUserStatus(list[position].copy(status = "rejected"),{
                },{
                    Log.d("TAG", "bindView: $it")
                })
            }
        }
    }

    override fun getItemViewType(position: Int): Int = if(classList[position].userType=="admin") 0 else 1

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            0 ->{
                return ViewHolderAdmin(
                    ItemAdminBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup, false), viewGroup.context, viewModel
                )
            }
            else ->{
                return ViewHolderShop(
                    ItemShopBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup, false), viewGroup.context, viewModel
                )
            }
        }
    }

    override fun getItemCount() = classList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            0 ->{
                (holder as ViewHolderAdmin).bindView(classList, position, listType)
            }
            else ->{
                (holder as ViewHolderShop).bindView(classList, position, listType)
            }
        }
    }
}
