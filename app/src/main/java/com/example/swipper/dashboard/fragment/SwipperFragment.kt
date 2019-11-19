package com.example.swipper.dashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swipper.R
import com.example.swipper.dashboard.CardsAdapter
import com.example.swipper.dashboard.interfaces.UserActionsCallback
import com.example.swipper.database.DATA_GENDER
import com.example.swipper.database.DATA_MATCHES
import com.example.swipper.database.DATA_SWIPES_LEFT
import com.example.swipper.database.DATA_SWIPES_RIGHT
import com.example.swipper.database.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swipes.*

class SwipperFragment : Fragment() {


    private var callback: UserActionsCallback?= null
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var cardsAdapter : ArrayAdapter<User>?= null
    private var rowItems = ArrayList<User>()
    private var preferredGender: String ? = null


    fun setCallback(callback : UserActionsCallback){
        this.callback = callback
        userId = callback.getUserId()
        userDatabase = callback.getUserDatabase()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User:: class.java)
                preferredGender = user?.preferredGender
                populateItems()

            }

        })
        cardsAdapter =CardsAdapter(context, R.layout.swipe_list_item, rowItems)

        frame.adapter = cardsAdapter

        frame.setFlingListener(object  : SwipeFlingAdapterView.onFlingListener{
            override fun removeFirstObjectInAdapter() {
                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                var user = p0 as User
                userDatabase.child(user.uid.toString()).child(DATA_SWIPES_LEFT).child(userId).setValue(true)
            }

            override fun onRightCardExit(p0: Any?) {
                val selectedUser = p0 as User
                val selectedUserId = selectedUser.uid
                if(!selectedUserId.isNullOrEmpty()){
                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT).addListenerForSingleValueEvent(object  : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.hasChild(selectedUserId)){
                                Toast.makeText(context, "Match!", Toast.LENGTH_SHORT).show()

                                userDatabase.child(userId).child(DATA_SWIPES_RIGHT).child(selectedUserId).removeValue()
                                userDatabase.child(userId).child(DATA_MATCHES).child(selectedUserId).setValue(true)
                                userDatabase.child(selectedUserId).child(DATA_MATCHES).child(userId).setValue(true)
                            }else {
                                userDatabase.child(selectedUserId).child(DATA_SWIPES_RIGHT).child(userId).setValue(true)
                            }
                        }

                    })
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }

        })

        frame.setOnItemClickListener{position, data -> }

        likeButton.setOnClickListener {
            if(!rowItems.isEmpty()){
                frame.topCardListener.selectRight()
            }
        }

        dislikeButton.setOnClickListener {
            if(!rowItems.isEmpty()){
                frame.topCardListener.selectLeft()
            }
        }
    }

    fun populateItems() {
        noUsersLayout.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE
        val cardQuery = userDatabase.orderByChild(DATA_GENDER).equalTo(preferredGender)
        cardQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    child ->
                    val user = child.getValue(User:: class.java)
                    if(user !=  null){
                        var showUser = true
                        if(child.child(DATA_SWIPES_LEFT).hasChild(userId) ||
                                child.child(DATA_SWIPES_RIGHT).hasChild(userId) ||
                                child.child(DATA_MATCHES).hasChild(userId)){
                            showUser = false
                        }

                        if(showUser) {
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progressLayout.visibility = View.GONE
                if(rowItems.isEmpty()){
                    noUsersLayout.visibility = View.VISIBLE
                }
            }

        })
    }
}
