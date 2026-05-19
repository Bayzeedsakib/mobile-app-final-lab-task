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
import com.example.universitynewsapp.adapter.PostAdapter
import com.example.universitynewsapp.model.User
import com.example.universitynewsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {
    private lateinit var userInitials: TextView
    private lateinit var userName: TextView
    private lateinit var userUsername: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView
    private lateinit var userWebsite: TextView
    private lateinit var userCompanyName: TextView
    private lateinit var userCompanyCatchPhrase: TextView
    private lateinit var userPostsRecyclerView: RecyclerView
    private lateinit var profileProgressBar: ProgressBar
    private lateinit var postsProgressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retryButton: android.widget.Button

    private val repository = PostRepository()
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        initializeViews()
        setupToolbar()
        setupRecyclerView()
        setupRetryButton()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            loadUserProfile(userId)
        }
    }

    private fun initializeViews() {
        userInitials = findViewById(R.id.userInitials)
        userName = findViewById(R.id.userName)
        userUsername = findViewById(R.id.userUsername)
        userEmail = findViewById(R.id.userEmail)
        userPhone = findViewById(R.id.userPhone)
        userWebsite = findViewById(R.id.userWebsite)
        userCompanyName = findViewById(R.id.userCompanyName)
        userCompanyCatchPhrase = findViewById(R.id.userCompanyCatchPhrase)
        userPostsRecyclerView = findViewById(R.id.userPostsRecyclerView)
        profileProgressBar = findViewById(R.id.profileProgressBar)
        postsProgressBar = findViewById(R.id.postsProgressBar)
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
        postAdapter = PostAdapter { post ->
            val intent = Intent(this, PostDetailActivity::class.java).apply {
                putExtra("POST_ID", post.id)
                putExtra("USER_ID", post.userId)
            }
            startActivity(intent)
        }
        userPostsRecyclerView.adapter = postAdapter
    }

    private fun setupRetryButton() {
        retryButton.setOnClickListener {
            val userId = intent.getIntExtra("USER_ID", -1)
            if (userId != -1) {
                loadUserProfile(userId)
            }
        }
    }

    private fun loadUserProfile(userId: Int) {
        profileProgressBar.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val user = repository.getUserById(userId)
                displayUserProfile(user)

                // Load user posts
                val posts = repository.getPostsByUser(userId)
                postsProgressBar.visibility = View.GONE
                postAdapter.submitList(posts)
            } catch (e: HttpException) {
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun displayUserProfile(user: User) {
        profileProgressBar.visibility = View.GONE

        // Generate and display initials
        val initials = generateInitials(user.name)
        userInitials.text = initials
        userInitials.setBackgroundColor(getColorForUser(user.id))

        // Display user information
        userName.text = user.name
        userUsername.text = "@${user.username}"
        userEmail.text = user.email
        userPhone.text = "Phone: ${user.phone}"
        userWebsite.text = "Website: ${user.website}"
        userCompanyName.text = user.company.name
        userCompanyCatchPhrase.text = "\"${user.company.catchPhrase}\""
    }

    private fun generateInitials(name: String): String {
        val parts = name.split(" ")
        return if (parts.size >= 2) {
            "${parts[0].first()}${parts[1].first()}".uppercase()
        } else {
            parts[0].take(2).uppercase()
        }
    }

    private fun getColorForUser(userId: Int): Int {
        val colors = intArrayOf(
            0xFFE57373.toInt(),
            0xFFF06292.toInt(),
            0xFFBA68C8.toInt(),
            0xFF9575CD.toInt(),
            0xFF7986CB.toInt(),
            0xFF64B5F6.toInt(),
            0xFF4FC3F7.toInt(),
            0xFF4DD0E1.toInt(),
            0xFF4DB6AC.toInt(),
            0xFF81C784.toInt()
        )
        return colors[(kotlin.math.abs(userId) % colors.size)]
    }

    private fun showError(message: String) {
        profileProgressBar.visibility = View.GONE
        postsProgressBar.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorText.text = message
    }
}

