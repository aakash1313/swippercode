package com.example.swipper.dashboard.interfaces

import com.google.firebase.database.DatabaseReference

interface UserActionsCallback {

    fun onSignOut()
    fun getUserId(): String
    fun getUserDatabase(): DatabaseReference
}