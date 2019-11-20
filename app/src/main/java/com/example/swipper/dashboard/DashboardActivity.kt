package com.example.swipper.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.swipper.R
import com.example.swipper.SwipperBaseActivity
import com.example.swipper.dashboard.fragment.MatchesFragment
import com.example.swipper.dashboard.fragment.ProfileFragment
import com.example.swipper.dashboard.fragment.SwipperFragment
import com.example.swipper.dashboard.interfaces.UserActionsCallback
import com.example.swipper.database.DATA_CHATS
import com.example.swipper.database.DATA_USERS
import com.example.swipper.onboardingscreen.StartupActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException

const val REQUEST_PHOTO_CODE = 1234

class DashboardActivity : SwipperBaseActivity(), UserActionsCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDetabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: SwipperFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipeTab: TabLayout.Tab? = null
    private var matchesTab: TabLayout.Tab? = null

    private var resultImageUrl: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()){
            onSignOut()
        }

        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        userDetabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)

        profileTab = navigation_tab.newTab()
        swipeTab = navigation_tab.newTab()
        matchesTab = navigation_tab.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        swipeTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_swipe)
        matchesTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_matches)

        navigation_tab.addTab(profileTab!!)
        navigation_tab.addTab(swipeTab!!)
        navigation_tab.addTab(matchesTab!!)

        navigation_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab) {
                    profileTab -> {
                        if (profileFragment == null) {
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@DashboardActivity)
                        }
                        replaceFragment(profileFragment!!)

                    }
                    swipeTab -> {

                        if (swipeFragment == null) {
                            swipeFragment = SwipperFragment()
                            swipeFragment!!.setCallback(this@DashboardActivity)
                        }
                        replaceFragment(swipeFragment!!)

                    }

                    matchesTab -> {

                        if (matchesFragment == null) {
                            matchesFragment = MatchesFragment()
                            matchesFragment!!.setCallback(this@DashboardActivity)
                        }
                        replaceFragment(matchesFragment!!)

                    }
                }
            }

        })

        profileTab?.select()

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onSignOut() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun getUserId(): String =  userId!!

    override fun getUserDatabase(): DatabaseReference = userDetabase

    override fun profileComplete() {
        swipeTab?.select()
    }

    override fun getChatDatabase(): DatabaseReference = chatDatabase

    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PHOTO_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_PHOTO_CODE){
            resultImageUrl = data?.data
            storeImage()
        }
    }

    private fun storeImage(){
        if(resultImageUrl != null && userId != null){
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            var bitmap: Bitmap?= null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            }catch (e: IOException){
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener { e -> e.printStackTrace()}
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl
                    .addOnSuccessListener { uri ->
                        profileFragment?.updateImageUri(uri.toString())
                    }
                    .addOnFailureListener { e -> e.printStackTrace() }
            }
        }
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, DashboardActivity::class.java)
    }

}
