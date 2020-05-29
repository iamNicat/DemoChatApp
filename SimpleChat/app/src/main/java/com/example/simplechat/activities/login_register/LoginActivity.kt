package com.example.simplechat.activities.login_register

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.simplechat.R
import com.example.simplechat.activities.chat_message.MainMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }

        btn_login.setOnClickListener {
            doLogin()

        }
        forget_password.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view: View = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val emailadress: EditText = view.findViewById<EditText>(R.id.forget_password_email)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                forgotPassword(emailadress)

            })
            builder.setNegativeButton("close", DialogInterface.OnClickListener { _, _ -> })
            builder.show()
        }

    }

    private fun forgotPassword(emailadress: EditText) {
        if (emailadress.text.toString().isEmpty()) {


            return

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailadress.text.toString()).matches()) {

        }
        auth.sendPasswordResetEmail(emailadress.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun doLogin() {
        if (editEmail.text.toString().isEmpty()) {
            editEmail.error = "Please enter email"
            editEmail.requestFocus()
            return

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.text.toString()).matches()) {
            editEmail.error = "Please enter valid email"
            editEmail.requestFocus()
            return

        }
        if (editPassword.text.toString().isEmpty()) {
            editPassword.error = "Please Enter password"
            editPassword.requestFocus()
            return

        }

        auth.signInWithEmailAndPassword(editEmail.text.toString(), editPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)

                } else {
                    Toast.makeText(
                        baseContext, "Login failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, MainMessagesActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    baseContext, "Please verify your email.",
                    Toast.LENGTH_SHORT
                ).show()

            }
        } else {
            Toast.makeText(
                baseContext, "Login failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
