package com.example.swipper.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.swipper.R
import com.example.swipper.SwipperBaseActivity
import com.example.swipper.dashboard.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : SwipperBaseActivity() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val fireAuthStateListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if(user != null){
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(fireAuthStateListener)
    }


    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(fireAuthStateListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUI()
    }

    private fun initUI(){
       login_btn.setOnClickListener { doLogin() }
    }

    private fun doLogin(){
        if(!email_edit_text.text.toString().isNullOrEmpty() && !password_edit_text.text.toString().isNullOrEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email_edit_text.text.toString(), password_edit_text.text.toString())
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful){
                        Toast.makeText(this, "User could not be logged in ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        println("I am here")
                    }
                }
        }

    }


    companion object {
        fun newIntent(context: Context?) = Intent(context, LoginActivity:: class.java)
    }
}
