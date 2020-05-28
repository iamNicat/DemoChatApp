package com.example.simplechat.activities.chat_message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.simplechat.R
import com.example.simplechat.activities.account_settings.AccountSettingsActivity
import com.example.simplechat.activities.login_register.LoginActivity
import com.example.simplechat.classes.chat.ChatMessage
import com.example.simplechat.classes.chat.LatestMessagesRow
import com.example.simplechat.classes.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainMessagesActivity : AppCompatActivity() {
    companion object {
        var currentUser: User? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessagesRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        btn_new_message.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        listenForLatestMessages()
        fetchCurrentUser()

        val uid = FirebaseAuth.getInstance().uid

    }

    val latestMessagesMap = HashMap<String, ChatMessage>()
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessagesRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest - messages/$fromID")
        ref.addChildEventListener(
            object : ChildEventListener {


                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages()


                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages()
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

                override fun onCancelled(p0: DatabaseError) {
                }


            }
        )
    }

    val adapter = GroupAdapter<GroupieViewHolder>()


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, AccountSettingsActivity::class.java))

            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
