package com.example.simplechat.classes

import com.example.simplechat.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatIFromItem : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout(): Int {
       return R.layout.chat_from_row
    }
}