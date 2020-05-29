package com.example.simplechat.activities.chat_message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.simplechat.R
import com.example.simplechat.classes.chat.ChatIFromItem
import com.example.simplechat.classes.chat.ChatIToItem
import com.example.simplechat.classes.chat.ChatMessage
import com.example.simplechat.classes.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser!!.username

        listenForMessages()
        send_button_chat_log.setOnClickListener {
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val fromID = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val reference =
            FirebaseDatabase.getInstance().getReference("/user - messages/$fromID/$toId")
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser =
                            MainMessagesActivity.currentUser

                        adapter.add(
                            ChatIFromItem(
                                chatMessage.text,
                                currentUser!!
                            )
                        )

                    } else {

                        adapter.add(
                            ChatIToItem(
                                chatMessage.text,
                                toUser!!
                            )
                        )

                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {


        val text = edittext_chat_log.text.toString()
        val fromID = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val toId = user.uid
        if (fromID == null) return
        val reference =
            FirebaseDatabase.getInstance().getReference("/user - messages/$fromID/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user - messages/$toId/$fromID").push()

        val chatMessage =
            ChatMessage(
                reference.key!!,
                text,
                fromID,
                toId,
                System.currentTimeMillis() / 1000
            )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)


        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest - messages/$fromID/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest - messages/$toId/$fromID")
        latestMessageToRef.setValue(chatMessage)


    }


}

