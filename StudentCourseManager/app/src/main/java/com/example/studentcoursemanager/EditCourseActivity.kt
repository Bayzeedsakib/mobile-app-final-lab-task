package com.example.studentcoursemanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditCourseActivity : AppCompatActivity() {

    private lateinit var course: Course
    private val studentNode: String by lazy {
        android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            ?: "anonymous"
    }
    // dbRef will be obtained locally inside methods to avoid top-level type inference issues

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        supportActionBar?.title = "Edit Course"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // populate spinners
        val creditsAdapter = ArrayAdapter.createFromResource(this, R.array.credits_array, android.R.layout.simple_spinner_item)
        creditsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spCredits = findViewById<Spinner>(R.id.spCredits)
        spCredits.adapter = creditsAdapter

        val semAdapter = ArrayAdapter.createFromResource(this, R.array.semesters_array, android.R.layout.simple_spinner_item)
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spSemester = findViewById<Spinner>(R.id.spSemester)
        spSemester.adapter = semAdapter

        val extra = intent.getSerializableExtra("course")
        if (extra is Course) {
            course = extra
            bindCourse()
        } else {
            Toast.makeText(this, "No course provided", Toast.LENGTH_SHORT).show()
            finish()
        }

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnCancel.setOnClickListener { finish() }
        btnSave.setOnClickListener { updateCourse() }
    }

    private fun bindCourse() {
        val etName = findViewById<EditText>(R.id.etName)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etInstructor = findViewById<EditText>(R.id.etInstructor)
        val etSchedule = findViewById<EditText>(R.id.etSchedule)
        val etRoom = findViewById<EditText>(R.id.etRoom)
        val spCredits = findViewById<Spinner>(R.id.spCredits)
        val spSemester = findViewById<Spinner>(R.id.spSemester)

        etName.setText(course.name)
        etCode.setText(course.code)
        etInstructor.setText(course.instructor)
        etSchedule.setText(course.schedule)
        etRoom.setText(course.room)
        // set credits spinner
        val creditsPos = (spCredits.adapter as ArrayAdapter<String>).getPosition(course.credits.toString())
        if (creditsPos >= 0) spCredits.setSelection(creditsPos)
        val semPos = (spSemester.adapter as ArrayAdapter<String>).getPosition(course.semester)
        if (semPos >= 0) spSemester.setSelection(semPos)
    }

    private fun updateCourse() {
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

        val updated = Course(id = course.id, name = name, code = code, instructor = instructor, credits = credits, schedule = schedule, room = room, semester = semester)
        val dbRef = FirebaseDatabase.getInstance().getReference("students").child(studentNode).child("courses")
        dbRef.child(course.id).setValue(updated).addOnCompleteListener { task ->
            showProgress(false)
            if (task.isSuccessful) {
                Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update course", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                confirmDelete()
                true
            }
            android.R.id.home -> {
                finish(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Delete") { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("students").child(studentNode).child("courses")
                dbRef.child(course.id).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showProgress(show: Boolean) {
        val pb: ProgressBar = findViewById(R.id.progressBar)
        pb.visibility = if (show) View.VISIBLE else View.GONE
    }
}






