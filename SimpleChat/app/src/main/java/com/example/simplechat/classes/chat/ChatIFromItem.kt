package com.example.simplechat.classes.chat

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.example.simplechat.R
import com.example.simplechat.activities.chat_message.NewMessageActivity
import com.example.simplechat.classes.user.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import java.util.*

class ChatIFromItem(val text: String, val user: User) : Item<GroupieViewHolder>() {


    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {




    viewHolder.itemView.textview_from_row.text =  text
    val uri = user.profileImageUrl
    val targetImageView = viewHolder.itemView.imageview_chat_from_row
    Picasso.get().load(uri).into(targetImageView)



    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}