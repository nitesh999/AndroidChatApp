package com.example.chatapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityLoginBinding
import com.example.chatapplication.network.NetworkUser
import com.example.chatapplication.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = ViewModelProviders.of(this, LoginViewModel.Factory()).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()
        initObservables()
    }

    private fun initObservables() {
        viewModel.showStatus.observe(this, Observer<String> { loginStatus ->
            if(!loginStatus.isNullOrEmpty()) {
                Toast.makeText(this@LoginActivity, loginStatus, Toast.LENGTH_LONG).show()
                tvUserStatus.text = loginStatus
            }
        })
        viewModel.userData.observe(this, Observer<NetworkUser> { user ->
            if(user!=null){
                Toast.makeText(this@LoginActivity, "Firebase Token Sent", Toast.LENGTH_LONG).show()
                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@LoginActivity, UserListActivity::class.java))
                ActivityCompat.finishAffinity(this@LoginActivity);
            }
        })
    }
}
