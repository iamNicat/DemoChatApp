package com.example.simplechat.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.simplechat.R
import com.example.simplechat.Classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        photo.setOnClickListener {
            Log.d("Register", "tRY TO SHOW PHOTO SELECTED")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register.setOnClickListener {

            registerUser()

        }

    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            photo_imageview_register.setImageBitmap(bitmap)
            photo.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            photo.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun registerUser() {
        if (edtFullName.text.toString().isEmpty()) {
            edtFullName.error = "Please enter full name"
            edtFullName.requestFocus()
            return
        }
        if (email.text.toString().isEmpty()) {
            email.error = "Please enter email"
            email.requestFocus()
            return

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Please enter valid email"
            email.requestFocus()
            return

        }
        if (password.text.toString().isEmpty()) {
            password.error = "Please Enter password"
            password.requestFocus()
            return

        }
        if (phonenumber.text.toString().isEmpty()) {
            phonenumber.error = "Please Enter phone number"
            phonenumber.requestFocus()
            return

        }
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user: FirebaseUser? = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                uploadImageToFirebaseStorage()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()

                            }
                        }


                } else {
                    Toast.makeText(
                        baseContext,
                        "Register process failed,Try again please!",
                        Toast.LENGTH_SHORT
                    ).show()


                }
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val mStorageRef = FirebaseStorage.getInstance().getReference("/images/$filename")
        mStorageRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                mStorageRef.downloadUrl.addOnSuccessListener {

                    saveUserToFDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }

    private fun saveUserToFDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid,
            edtFullName.text.toString(),
            profileImageUrl
        )
        reference.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully registred firebase: ")

            }
    }

}

