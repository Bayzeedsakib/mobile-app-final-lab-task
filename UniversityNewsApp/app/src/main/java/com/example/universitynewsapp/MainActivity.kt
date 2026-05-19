package com.example.universitynewsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.universitynewsapp.adapter.PostAdapter
import com.example.universitynewsapp.model.Post
import com.example.universitynewsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var errorLayout: android.widget.LinearLayout
    private lateinit var errorText: android.widget.TextView
    private lateinit var retryButton: android.widget.Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView

    private val repository = PostRepository()
    private lateinit var adapter: PostAdapter
    private var allPosts: List<Post> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupSearchView()
        setupSwipeRefresh()
        setupRetryButton()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadPosts()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchView = findViewById(R.id.searchView)
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter { post ->
            val intent = Intent(this, PostDetailActivity::class.java).apply {
                putExtra("POST_ID", post.id)
                putExtra("USER_ID", post.userId)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText ?: "")
                return true
            }
        })
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            loadPosts()
        }
    }

    private fun setupRetryButton() {
        retryButton.setOnClickListener {
            loadPosts()
        }
    }

    private fun loadPosts() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false

        lifecycleScope.launch {
            try {
                allPosts = repository.getAllPosts()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(allPosts)
            } catch (e: HttpException) {
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun filterPosts(query: String) {
        val filteredPosts = if (query.isEmpty()) {
            allPosts
        } else {
            allPosts.filter { post ->
                post.title.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filteredPosts)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorText.text = message
        swipeRefreshLayout.isRefreshing = false
    }
}

