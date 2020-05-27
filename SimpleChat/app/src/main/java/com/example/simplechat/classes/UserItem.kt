package com.example.simplechat.classes

import com.example.simplechat.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user:User) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
      viewHolder.itemView.username_tv_new_message.text = user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}