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
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.adapter.CommentAdapter
import com.example.universitynewsapp.model.Comment
import com.example.universitynewsapp.model.Post
import com.example.universitynewsapp.model.User
import com.example.universitynewsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostDetailActivity : AppCompatActivity() {
    private lateinit var postTitle: TextView
    private lateinit var postBody: TextView
    private lateinit var authorCard: CardView
    private lateinit var authorName: TextView
    private lateinit var authorEmail: TextView
    private lateinit var authorCompany: TextView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var postProgressBar: ProgressBar
    private lateinit var authorProgressBar: ProgressBar
    private lateinit var commentsProgressBar: ProgressBar

    private val repository = PostRepository()
    private var currentPost: Post? = null
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post_detail)

        initializeViews()
        setupToolbar()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val postId = intent.getIntExtra("POST_ID", -1)
        if (postId != -1) {
            loadPostDetails(postId)
        }
    }

    private fun initializeViews() {
        postTitle = findViewById(R.id.postTitle)
        postBody = findViewById(R.id.postBody)
        authorCard = findViewById(R.id.authorCard)
        authorName = findViewById(R.id.authorName)
        authorEmail = findViewById(R.id.authorEmail)
        authorCompany = findViewById(R.id.authorCompany)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        postProgressBar = findViewById(R.id.postProgressBar)
        authorProgressBar = findViewById(R.id.authorProgressBar)
        commentsProgressBar = findViewById(R.id.commentsProgressBar)

        commentsRecyclerView.adapter = CommentAdapter()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadPostDetails(postId: Int) {
        lifecycleScope.launch {
            try {
                // Load post
                currentPost = repository.getPostById(postId)
                currentPost?.let { post ->
                    postProgressBar.visibility = View.GONE
                    postTitle.text = post.title
                    postBody.text = post.body

                    // Load author
                    loadAuthor(post.userId)
                }

                // Load comments
                val comments = repository.getCommentsByPost(postId)
                commentsProgressBar.visibility = View.GONE
                (commentsRecyclerView.adapter as CommentAdapter).submitList(comments)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun loadAuthor(userId: Int) {
        lifecycleScope.launch {
            try {
                currentUser = repository.getUserById(userId)
                currentUser?.let { user ->
                    authorProgressBar.visibility = View.GONE
                    authorCard.visibility = View.VISIBLE
                    authorName.text = user.name
                    authorEmail.text = user.email
                    authorCompany.text = "Company: ${user.company.name}"

                    authorCard.setOnClickListener {
                        val intent = Intent(this@PostDetailActivity, UserProfileActivity::class.java).apply {
                            putExtra("USER_ID", user.id)
                        }
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                authorProgressBar.visibility = View.GONE
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is HttpException -> postTitle.text = "Error: ${exception.code()}"
            is IOException -> postTitle.text = "Network error"
            else -> postTitle.text = "Error: ${exception.message}"
        }
    }
}

