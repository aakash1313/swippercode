package com.example.swipper.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.swipper.R
import com.example.swipper.SwipperBaseActivity
import com.example.swipper.dashboard.DashboardActivity
import com.example.swipper.database.DATA_USERS
import com.example.swipper.database.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : SwipperBaseActivity() {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if(user != null){
            startActivity(DashboardActivity.newIntent(this))
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }


    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    private fun initUI(){
        signup_btn.setOnClickListener {
            createUser()
        }
    }


    private fun createUser(){
        if(!email_edit_text.text.toString().isNullOrEmpty() && !password_edit_text.text.toString().isNullOrEmpty()){
            firebaseAuth.createUserWithEmailAndPassword(email_edit_text.text.toString(), password_edit_text.text.toString())
                .addOnCompleteListener {
                    if(!it.isSuccessful){
                       Toast.makeText(this, "User could not be added ${it.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        val email = email_edit_text.text.toString()
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        val user = User(uid = userId, email = email)
                        firebaseDatabase.child(DATA_USERS).child(userId).setValue(user)

                    }
                }
        }
    }




    companion object {
        fun newIntent(context: Context?) = Intent(context, SignUpActivity:: class.java)
    }
}
