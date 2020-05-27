package com.example.simplechat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplechat.R
import com.example.simplechat.classes.ChatIFromItem
import com.example.simplechat.classes.ChatIToItem
import com.example.simplechat.classes.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

       val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username

        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(ChatIFromItem())
//        adapter.add(ChatIToItem())

        recyclerview_chat_log.adapter = adapter
    }
}

