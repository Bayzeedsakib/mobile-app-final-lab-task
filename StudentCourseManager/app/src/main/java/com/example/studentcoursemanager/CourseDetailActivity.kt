package com.example.studentcoursemanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CourseDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        supportActionBar?.title = "Course Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val course = intent.getSerializableExtra("course") as? Course ?: return

        findViewById<TextView>(R.id.tvName).text = course.name
        findViewById<TextView>(R.id.tvCode).text = course.code
        findViewById<TextView>(R.id.tvInstructor).text = course.instructor
        findViewById<TextView>(R.id.tvCredits).text = "${course.credits}"
        findViewById<TextView>(R.id.tvSchedule).text = course.schedule
        findViewById<TextView>(R.id.tvRoom).text = course.room
        findViewById<TextView>(R.id.tvSemester).text = course.semester
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

