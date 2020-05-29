package com.example.simplechat.activities.account_settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.simplechat.R
import com.example.simplechat.activities.chat_message.MainMessagesActivity
import com.example.simplechat.activities.login_register.LoginActivity
import com.example.simplechat.classes.user.User
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.user_row_new_message.*

class AccountSettingsActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)




        btn_change_password.setOnClickListener {
            changePassword()


        }
        btn_change_user_info.setOnClickListener {
            changeUserInfo()
        }
    }




    private fun changeUserInfo() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    val username = setting_new_username.text.toString()
                    val phone = setting_new_phone.text.toString()

                    ref.setValue(User(user.uid, username, phone, user.profileImageUrl))
                        .addOnSuccessListener {
                            startActivity(
                                Intent(
                                    this@AccountSettingsActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()
                        }


                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    private fun changePassword() {

        if (setting_current_password.text.isNotEmpty() &&
            setting_new_password.text.isNotEmpty() &&
            setting_confirm_password.text.isNotEmpty()
        ) {
            if (setting_new_password.text.toString()
                    .equals(setting_confirm_password.text.toString())
            ) {


                if (user != null && user.email != null) {
                    val credential: AuthCredential = EmailAuthProvider.getCredential(
                        user.email!!,

                        setting_current_password.text.toString()
                    )

                    user?.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            user?.updatePassword(setting_new_password.text.toString())
                                ?.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Changes are saved successfuly !",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        auth.signOut()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    }
                                }

                        } else {
                            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()

                        }
                    }

                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "Password is not same!", Toast.LENGTH_SHORT).show()


            }

        } else {
            Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show()
        }
    }
}
