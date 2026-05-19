package com.example.universitynewsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.adapter.UserAdapter
import com.example.universitynewsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UsersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retryButton: android.widget.Button

    private val repository = PostRepository()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_users)

        initializeViews()
        setupToolbar()
        setupRecyclerView()
        setupRetryButton()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadUsers()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter { user ->
            val intent = Intent(this, UserProfileActivity::class.java).apply {
                putExtra("USER_ID", user.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun setupRetryButton() {
        retryButton.setOnClickListener {
            loadUsers()
        }
    }

    private fun loadUsers() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val users = repository.getAllUsers()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(users)
            } catch (e: HttpException) {
                val status = e.response()?.code() ?: -1
                showError("Server error: $status")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorText.text = message
    }
}



