package com.example.swipper.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.swipper.R
import com.example.swipper.SwipperBaseActivity
import com.example.swipper.dashboard.fragment.MatchesFragment
import com.example.swipper.dashboard.fragment.ProfileFragment
import com.example.swipper.dashboard.fragment.SwipperFragment
import com.example.swipper.dashboard.interfaces.UserActionsCallback
import com.example.swipper.database.DATA_USERS
import com.example.swipper.onboardingscreen.StartupActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class DashboardActivity : SwipperBaseActivity(), UserActionsCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDetabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: SwipperFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipeTab: TabLayout.Tab? = null
    private var matchesTab: TabLayout.Tab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()){
            onSignOut()
        }

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

    companion object {
        fun newIntent(context: Context?) = Intent(context, DashboardActivity::class.java)
    }

}
