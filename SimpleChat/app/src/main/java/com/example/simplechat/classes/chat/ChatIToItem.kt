package com.example.simplechat.classes.chat

import com.example.simplechat.R
import com.example.simplechat.classes.user.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatIToItem(val text:String,val user: User) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
          viewHolder.itemView.textview_to_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
       return R.layout.chat_to_row
    }
}