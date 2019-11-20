package com.example.swipper.dashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swipper.R
import com.example.swipper.chat.adapter.ChatsAdapter
import com.example.swipper.chat.models.Chat
import com.example.swipper.dashboard.interfaces.UserActionsCallback
import com.example.swipper.database.DATA_MATCHES
import com.example.swipper.database.user.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment : Fragment() {


    private lateinit var userId : String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private  var callback: UserActionsCallback? = null
    private val chatsAdapter = ChatsAdapter(ArrayList())


    fun setCallback(callback : UserActionsCallback){
        this.callback = callback
        userId = callback?.getUserId()
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()

        fetchData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchesRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }
    }

    fun fetchData(){
        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChildren()){
                    p0.children.forEach { child ->
                        val matchId = child.key
                        val chatId = child.value.toString()
                        if(!matchId.isNullOrEmpty()){
                            userDatabase.child(matchId).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = p0.getValue(User::class.java)
                                    if(user!= null){
                                        val chat = Chat(userId, chatId, user.uid, user.name, user.imageUrl)
                                        chatsAdapter.addElement(chat)
                                    }
                                }

                            })
                        }
                    }
                }
            }

        })
    }
}
