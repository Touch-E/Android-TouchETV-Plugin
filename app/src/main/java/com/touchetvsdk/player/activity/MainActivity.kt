package com.touchetvsdk.player.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.touchetv.player.api.response.AllContentsResponse
import com.touchetv.player.ui.activity.AccountDetailsActivity
import com.touchetv.player.ui.activity.CartActivity
import com.touchetv.player.ui.activity.MovieDetailActivity
import com.touchetv.player.ui.adapter.ViewPagerAdapter
import com.touchetv.player.utils.OpenClass
import com.touchetv.player.utils.SessionManager
import com.touchetvsdk.player.R
import com.touchetvsdk.player.databinding.ActivityMainBinding
import com.touchetvsdk.player.setPreviewBothSide
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeScreen: OpenClass

    private var data: List<AllContentsResponse>? = null

    val urlString = "https://api-cluster.system.touchetv.com"
    var userToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        showLoading()
        homeScreen = OpenClass(this)

        userToken = SessionManager.getToken(this).orEmpty()

        homeScreen.validateURLAndToken(urlString, userToken) { isURLValid, isTokenValid ->
            if (isURLValid && isTokenValid) {
                fetchMovieDetails()
                fetchCartDataCount()
            } else {
                navigateToLogin()
            }
        }

        binding.ivUser.setOnClickListener {
            startActivity(Intent(this, AccountDetailsActivity::class.java))
        }

        binding.ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.cardViewUserLayout.setOnClickListener { binding.ivUser.performClick() }

        binding.ivCartLayout.setOnClickListener { binding.ivCart.performClick() }
    }

    private fun fetchMovieDetails() {
        lifecycleScope.launch {
            homeScreen.getMovieDetail {
                processData(it)
            }
        }
    }

    private fun fetchCartDataCount() {
        lifecycleScope.launch {
            homeScreen.getCartDataCount { cartData ->
                cartData?.let {
                    binding.tvCartItemsNumber.visibility =
                        if (it.isNotEmpty()) View.VISIBLE else View.GONE
                    binding.tvCartItemsNumber.text = " ${it.size} "
                }
                stopLoading()
            }
        }
    }

    private fun processData(responses: List<AllContentsResponse>?) {
        if (!responses.isNullOrEmpty()) {
            data = responses
            init(responses)

            val userImageUrl = SessionManager.getUserImageUrl(this)
            if (userImageUrl != null) {
                if (userImageUrl.isNotEmpty()) {
                    Glide.with(this).load(userImageUrl).into(binding.ivUser)
                }
            }
        }
        stopLoading()
    }

    private fun init(data: List<AllContentsResponse>) {
        val adapter = ViewPagerAdapter(data) { movieID ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movieID", movieID)
            startActivity(intent)
        }

        binding.viewPager2.setOnClickListener {
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movieID", data[binding.viewPager2.currentItem].id)
            startActivity(intent)
        }

        binding.viewPager2.apply {
            this.adapter = adapter
            setPreviewBothSide(R.dimen._40sdp, R.dimen._50sdp)
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 10
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCartDataCount()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivitySecondary::class.java))
        finish()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.progressBar.visibility = View.GONE
    }
}