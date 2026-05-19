package com.example.studentcoursemanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import java.util.*

class AddCourseActivity : AppCompatActivity() {

    private val studentNode: String by lazy {
        android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            ?: "anonymous"
    }
    private val dbRef by lazy {
        FirebaseDatabase.getInstance().getReference("students").child(studentNode).child("courses")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        supportActionBar?.title = "Add Course"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val creditsAdapter = ArrayAdapter.createFromResource(this, R.array.credits_array, android.R.layout.simple_spinner_item)
        creditsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spCredits = findViewById<Spinner>(R.id.spCredits)
        spCredits.adapter = creditsAdapter

        val semAdapter = ArrayAdapter.createFromResource(this, R.array.semesters_array, android.R.layout.simple_spinner_item)
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spSemester = findViewById<Spinner>(R.id.spSemester)
        spSemester.adapter = semAdapter

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnCancel.setOnClickListener { finish() }
        btnSave.setOnClickListener { saveCourse() }
    }

    private fun saveCourse() {
        val etName = findViewById<EditText>(R.id.etName)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etInstructor = findViewById<EditText>(R.id.etInstructor)
        val spCredits = findViewById<Spinner>(R.id.spCredits)
        val etSchedule = findViewById<EditText>(R.id.etSchedule)
        val etRoom = findViewById<EditText>(R.id.etRoom)
        val spSemester = findViewById<Spinner>(R.id.spSemester)

        val name = etName.text.toString().trim()
        val code = etCode.text.toString().trim()
        val instructor = etInstructor.text.toString().trim()
        val credits = spCredits.selectedItem.toString().toIntOrNull() ?: 0
        val schedule = etSchedule.text.toString().trim()
        val room = etRoom.text.toString().trim()
        val semester = spSemester.selectedItem.toString()

        if (name.isEmpty() || code.isEmpty() || instructor.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress(true)

        val key = dbRef.push().key ?: UUID.randomUUID().toString()
        val course = Course(id = key, name = name, code = code, instructor = instructor, credits = credits, schedule = schedule, room = room, semester = semester)

        dbRef.child(key).setValue(course).addOnCompleteListener { task ->
            showProgress(false)
            if (task.isSuccessful) {
                Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgress(show: Boolean) {
        val pb: ProgressBar = findViewById(R.id.progressBar)
        pb.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}


