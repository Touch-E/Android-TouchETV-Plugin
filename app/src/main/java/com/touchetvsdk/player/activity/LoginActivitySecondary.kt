package com.touchetvsdk.player.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.touchetv.player.api.request.LoginRequest
import com.touchetv.player.api.response.LoginResponse
import com.touchetv.player.utils.OpenClass
import com.touchetvsdk.player.databinding.ActivityLoginSecondaryBinding

class LoginActivitySecondary : AppCompatActivity() {

    private lateinit var binding: ActivityLoginSecondaryBinding
    private lateinit var login: OpenClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSecondaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setClick()
    }

    private fun setClick() {
        login = OpenClass(this)

        binding.btnLogin.setOnClickListener {
            showLoading()

            val loginRequest = LoginRequest(
                password = binding.edtPassword.text.toString().trim(),
                email = binding.edtEmail.text.toString().trim()
            )

            login.userAuthentication(loginRequest) { response ->
                if (response?.isSuccessful == true) {
                    processLogin(response.body())
                } else {
                    stopLoading()

                    Toast.makeText(this, "Error logging you in", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processLogin(data: LoginResponse?) {
        if (data != null) {
            stopLoading()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            stopLoading()

            Toast.makeText(this, "Error logging you in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.progressBar.visibility = View.GONE
    }
}