package com.example.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.DateFormat
import java.util.Date

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val emailView = findViewById<TextView>(R.id.tvEmail)
        val uidView = findViewById<TextView>(R.id.tvUid)
        val createdView = findViewById<TextView>(R.id.tvCreated)
        val avatar = findViewById<TextView>(R.id.tvAvatar)

        emailView.text = user.email
        uidView.text = user.uid.take(8)
        val created = user.metadata?.creationTimestamp ?: 0L
        createdView.text = if (created > 0) DateFormat.getDateTimeInstance().format(Date(created)) else "-"
        avatar.text = user.email?.firstOrNull()?.uppercaseChar().toString()

        val logoutBtn = findViewById<Button>(R.id.btnLogout)
        val updateBtn = findViewById<Button>(R.id.btnUpdatePassword)
        val deleteBtn = findViewById<Button>(R.id.btnDeleteAccount)
        val newPassInput = findViewById<EditText>(R.id.inputNewPassword)
        val confirmPassInput = findViewById<EditText>(R.id.inputConfirmPassword)

        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        updateBtn.setOnClickListener { v ->
            val newPass = newPassInput.text.toString()
            val confirm = confirmPassInput.text.toString()
            if (newPass.length < 8) {
                Snackbar.make(v, "Password must be at least 8 characters", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass != confirm) {
                Snackbar.make(v, "Passwords do not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            user.updatePassword(newPass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(v, task.exception?.localizedMessage ?: "Failed to update password", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        deleteBtn.setOnClickListener { v ->
            AlertDialog.Builder(this)
                .setTitle("Delete account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Snackbar.make(v, task.exception?.localizedMessage ?: "Failed to delete account", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}

