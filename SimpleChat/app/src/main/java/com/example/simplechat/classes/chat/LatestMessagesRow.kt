package com.example.simplechat.classes.chat

import com.example.simplechat.R
import com.example.simplechat.classes.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessagesRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    var chatPartnerUser: User? =null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_latest_message.text = chatMessage.text
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textview_username_latest_message.text  = chatPartnerUser?.username
                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)

            }

            override fun onCancelled(p0: DatabaseError) {
            }


        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }


}