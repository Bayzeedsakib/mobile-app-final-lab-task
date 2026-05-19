package com.example.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        val fullNameInput = findViewById<EditText>(R.id.inputFullName)
        val emailInput = findViewById<EditText>(R.id.inputEmail)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val confirmInput = findViewById<EditText>(R.id.inputConfirmPassword)
        val registerBtn = findViewById<Button>(R.id.btnRegister)
        val loginLink = findViewById<TextView>(R.id.linkLogin)
        val progress = findViewById<ProgressBar>(R.id.progress)

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn.setOnClickListener {
            val name = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString()
            val confirm = confirmInput.text.toString()

            // Validation
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Snackbar.make(registerBtn, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Snackbar.make(registerBtn, "Enter a valid email", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 8) {
                Snackbar.make(registerBtn, "Password must be at least 8 characters", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != confirm) {
                Snackbar.make(registerBtn, "Passwords do not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    progress.visibility = View.GONE
                    if (task.isSuccessful) {
                        // update display name
                        val user = auth.currentUser
                        val profile = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profile)

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Snackbar.make(registerBtn, task.exception?.localizedMessage ?: "Registration failed", Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }
}

