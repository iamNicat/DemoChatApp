package com.example.simplechat.activities.account_settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
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
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.user_row_new_message.*
import java.util.*

class AccountSettingsActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser
    val currentuser = MainMessagesActivity.currentUser
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        supportActionBar?.title = "Settings"
         val uri = currentuser?.profileImageUrl
        Picasso.get().load(uri).into(setting_change_image)
       val username = currentuser?.username
        setting_new_username.hint = "username: $username"
        setting_new_phone.hint = "phone: ${currentuser?.phone}"

        setting_change_image.setOnClickListener {
            Log.d("Settings", "TRY TO SHOW PHOTO SELECTED")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)


        }
        btn_change_password.setOnClickListener {
            changePassword()
        }
        btn_change_user_info.setOnClickListener {
           changeUserInfo()
        }

    }
    private var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            setting_change_image.setImageBitmap(bitmap)
            setting_progress_bar.visibility = View.VISIBLE
            uploadImageToFirebaseStorage()

        }
    }


    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            Log.d("Settings","null")
            return
        }


        val filename = UUID.randomUUID().toString()
        val mStorageRef = FirebaseStorage.getInstance().getReference("/images/$filename")
        mStorageRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                mStorageRef.downloadUrl.addOnSuccessListener {
                    Log.d("Settings","Storage")

                    changeUserImage(it.toString())
                //    saveUserToFDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }

    }

    private fun changeUserImage(profileImageUrl1: String){
        ref.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(p0: DataSnapshot) {
               val user = p0.getValue(User::class.java)
                if(user != null){
                    Log.d("Settings","ChangeUserImage")
                    ref.setValue(User(user.uid,user.username,user.phone,profileImageUrl1))
                        .addOnSuccessListener {
                            setting_progress_bar.visibility = View.INVISIBLE
                            Toast.makeText(this@AccountSettingsActivity, "Profil picture changed successfully!", Toast.LENGTH_SHORT).show()

                        }
                }

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun changeUserInfo() {

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {

                    if(setting_new_username.text.isNotEmpty() &&  setting_new_phone.text.isNotEmpty()){
                        val username = setting_new_username.text.toString()
                        val phone = setting_new_phone.text.toString()



                        ref.setValue(User(user.uid, username, phone,user.profileImageUrl))
                            .addOnSuccessListener {
                                startActivity(
                                    Intent(
                                        this@AccountSettingsActivity,
                                        MainMessagesActivity::class.java
                                    )
                                )
                                finish()
                            }
                    } else{
                        setting_new_username.error = "Please fill both of them"
                        setting_new_phone.error = "Please fill both of them"

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
                        setting_current_password.text.toString())


                         userReauthenticate(credential)
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

    private fun userReauthenticate(credential: AuthCredential) {
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

    }
}
