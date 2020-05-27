package com.example.simplechat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplechat.classes.User
import com.example.simplechat.classes.UserItem
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
companion object{
    val USER_KEY = "USER_KEY"
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
              adapter.setOnItemClickListener { item, view ->

                  val userItem = item as UserItem
                  val intent = Intent (view.context,ChatLogActivity::class.java)
                  intent.putExtra(USER_KEY,userItem.user)
                  startActivity(intent)
                  finish()
              }
              recyclerview_newmessage.adapter = adapter
          }
          override fun onCancelled(p0: DatabaseError) {

          }
      })
    }
}
