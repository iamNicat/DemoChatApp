package com.example.simplechat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplechat.Classes.User
import com.example.simplechat.Classes.UserItem
import com.example.simplechat.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message_acitivty.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message_acitivty)
        supportActionBar?.title = "Select User"
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(UserItem())
//        recyclerview_newmessage.adapter = adapter
        fetchUsers()
    }

    private fun fetchUsers() {
      val reference =  FirebaseDatabase.getInstance().getReference("/users")
      reference.addListenerForSingleValueEvent(object:ValueEventListener{
          override fun onDataChange(p0: DataSnapshot) {
              val adapter = GroupAdapter<GroupieViewHolder>()
              p0.children.forEach{
                  val user = it.getValue(User::class.java)
                  if(user != null){
                      adapter.add(UserItem(user))

                  }
              }
              recyclerview_newmessage.adapter = adapter
          }
          override fun onCancelled(p0: DatabaseError) {

          }
      })
    }
}
