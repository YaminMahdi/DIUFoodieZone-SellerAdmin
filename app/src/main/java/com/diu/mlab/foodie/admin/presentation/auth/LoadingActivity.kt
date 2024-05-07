package com.diu.mlab.foodie.admin.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.diu.mlab.foodie.admin.databinding.ActivityLoadingBinding
import com.diu.mlab.foodie.admin.presentation.main.admin.AdminMainActivity
import com.diu.mlab.foodie.admin.presentation.main.admin.AdminMainViewModel
import com.diu.mlab.foodie.admin.presentation.main.admin.PendingActivity
import com.diu.mlab.foodie.admin.presentation.main.seller.SellerMainActivity
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding
    private val viewModel : AdminMainViewModel by viewModels()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingBinding.inflate(layoutInflater)

//        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
//        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(binding.root)

//        ReturnTransitionPatcher.patchAll(application)
//        window.sharedElementEnterTransition = ChangeBounds().setDuration(1000000000000000000)
//        window.sharedElementExitTransition = ChangeBounds().setDuration(1000000000000000000)
        val fade = Fade()
//        fade.duration = 200
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade

//        intent.putExtra(LoginActivity.EXTRA_CONTACT, contact)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(this@LoadingActivity,
                binding.logo as View,
                "logo" //ViewCompat.getTransitionName(binding.logo)!!
            )
        val emailId = Firebase.auth.currentUser?.email?.transformedEmailId()

        if(emailId != null){
            viewModel.getMyProfile(emailId){
                Log.d("TAG", "onCreate: $it")
            }
        }else{
            GlobalScope.launch(Dispatchers.Main){
                delay(500L)
                startActivity(Intent(this@LoadingActivity, LoginActivity::class.java))
                finish()
            }
        }

        viewModel.myProfile.observe(this){ user->
            Log.d("TAG", "onCreate: myProfile observe $user")
            if(user.status != "accepted")
                startActivity(Intent(this, PendingActivity::class.java))
            else if(user.userType=="admin")
                startActivity(Intent(this, AdminMainActivity::class.java))
            else if(user.userType=="shop")
                startActivity(Intent(this, SellerMainActivity::class.java))
            finish()
        }

    }
}