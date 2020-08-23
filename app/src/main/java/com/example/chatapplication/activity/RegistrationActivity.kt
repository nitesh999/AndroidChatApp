package com.example.chatapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityRegistrationBinding
import com.example.chatapplication.network.NetworkUser
import com.example.chatapplication.viewmodels.LoginViewModel
import com.example.chatapplication.viewmodels.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)

        viewModel = ViewModelProviders.of(this, LoginViewModel.Factory()).get(RegistrationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()
        initObservables()
    }

    private fun initObservables() {
        viewModel.showStatus.observe(this, Observer<String> { registrationStatus ->
            if(!registrationStatus.isNullOrEmpty()) {
                Toast.makeText(this@RegistrationActivity, registrationStatus, Toast.LENGTH_LONG).show()
                tvUserStatus.text = registrationStatus
            }
        })
        viewModel.userData.observe(this, Observer<NetworkUser> { user ->
            if(user!=null){
                Toast.makeText(this@RegistrationActivity, "Firebase Token Sent", Toast.LENGTH_LONG).show()
                Toast.makeText(this@RegistrationActivity, "Registration Successful, Please Login", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            }
        })
    }
}