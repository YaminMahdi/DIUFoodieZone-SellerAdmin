package com.diu.mlab.foodie.admin.presentation.main.admin

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.databinding.ActivityAdminMainBinding
import com.diu.mlab.foodie.admin.util.changeStatusBarColor
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {
    private val viewModel : AdminMainViewModel by viewModels()
    private lateinit var binding: ActivityAdminMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val manager: FragmentManager = supportFragmentManager

        binding.bubbleTabBar.addBubbleListener { id ->
            when(id){
                R.id.pending -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.tia))
                    this.changeStatusBarColor(R.color.tiaX,false)
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, RequestFragment.newInstance("pending"))
                        .commit()
                }
                R.id.rejected -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.redPop))
                    this.changeStatusBarColor(R.color.redZ,false)
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, RequestFragment.newInstance("rejected"))
                        .commit()
                }
                R.id.accepted -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.greenPop))
                    this.changeStatusBarColor(R.color.greenZ,false)
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, RequestFragment.newInstance("accepted"))
                        .commit()
                }
                R.id.profile -> {
                    binding.topView.setBackgroundColor(this.getColor(R.color.tia))
                    this.changeStatusBarColor(R.color.tiaX,false)
                    manager.beginTransaction()
                        .replace(binding.regFragment.id, RequestFragment.newInstance("profile"))
                        .commit()
                }
            }
        }
//        binding.bubbleTabBar
//            .setSelected(1,true)


    }

}