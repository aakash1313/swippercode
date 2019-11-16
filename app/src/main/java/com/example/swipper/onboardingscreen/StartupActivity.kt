package com.example.swipper.onboardingscreen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.swipper.R
import com.example.swipper.SwipperBaseActivity
import com.example.swipper.login.LoginActivity
import com.example.swipper.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_startup.*

class StartupActivity : SwipperBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        initUI()
    }

    private fun initUI(){
        login_btn.setOnClickListener { startActivity(LoginActivity.newIntent(this))}
        signup_btn.setOnClickListener { startActivity(SignUpActivity.newIntent(this))}
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, StartupActivity::class.java)
    }


}
