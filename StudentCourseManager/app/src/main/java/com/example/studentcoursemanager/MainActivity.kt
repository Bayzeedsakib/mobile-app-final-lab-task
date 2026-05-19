package com.example.studentcoursemanager

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), CourseAdapter.Listener {

    private lateinit var adapter: CourseAdapter
    private val courses = mutableListOf<Course>()
    private val studentNode: String by lazy {
        // Use device ANDROID_ID as a demo student identifier when not using Firebase Auth
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "anonymous"
    }

    private val dbRef by lazy {
        FirebaseDatabase.getInstance().getReference("students").child(studentNode).child("courses")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyState = findViewById<View>(R.id.emptyState)

        adapter = CourseAdapter(courses, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddCourseActivity::class.java))
        }

        swipeRefresh.setOnRefreshListener { loadCourses() }

        loadCourses()
    }

    private fun loadCourses() {
        val swipeRefresh = findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyState = findViewById<View>(R.id.emptyState)
        swipeRefresh.isRefreshing = true
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Course>()
                for (child in snapshot.children) {
                    val course = child.getValue(Course::class.java)
                    course?.let { list.add(it) }
                }
                courses.clear()
                courses.addAll(list)
                adapter.updateList(courses)
                emptyState.visibility = if (courses.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                swipeRefresh.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                swipeRefresh.isRefreshing = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as? SearchView
        searchView?.queryHint = "Search by name or code"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
        return true
    }

    override fun onEdit(course: Course) {
        val intent = Intent(this, EditCourseActivity::class.java)
        intent.putExtra("course", course)
        startActivity(intent)
    }

    override fun onDelete(course: Course) {
        // simple confirmation
        val key = course.id
        if (key.isNotEmpty()) {
            dbRef.child(key).removeValue()
        }
    }

    override fun onOpen(course: Course) {
        val intent = Intent(this, CourseDetailActivity::class.java)
        intent.putExtra("course", course)
        startActivity(intent)
    }
}