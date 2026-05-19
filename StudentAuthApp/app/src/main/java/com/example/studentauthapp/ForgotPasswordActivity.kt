package com.example.studentauthapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.tasks.Task

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: com.google.firebase.auth.FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        auth = com.google.firebase.auth.FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.inputEmail)
        val sendBtn = findViewById<Button>(R.id.btnSendReset)
        val progress = findViewById<ProgressBar>(R.id.progress)

        sendBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Snackbar.make(sendBtn, "Enter your email", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            progress.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task: Task<Void> ->
                    progress.visibility = View.GONE
                    if (task.isSuccessful) {
                        Snackbar.make(sendBtn, "Reset email sent. Check your inbox.", Snackbar.LENGTH_LONG).show()
                        finish()
                    } else {
                        Snackbar.make(sendBtn, task.exception?.localizedMessage ?: "Failed to send reset email", Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }
}


