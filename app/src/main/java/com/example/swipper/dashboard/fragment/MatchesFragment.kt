package com.example.swipper.dashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.swipper.R
import com.example.swipper.dashboard.interfaces.UserActionsCallback

class MatchesFragment : Fragment() {


    private var callback: UserActionsCallback?= null


    fun setCallback(callback : UserActionsCallback){
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }
}
