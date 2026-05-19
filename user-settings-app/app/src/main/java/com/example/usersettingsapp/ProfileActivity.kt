package com.university.usersettings

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    companion object {

        const val PROFILE_PREFS = "Profile Prefs"

        const val KEY_STUDENT_NAME = "KEY_STUDENT_NAME"
        const val KEY_STUDENT_ID = "KEY_STUDENT_ID"
        const val KEY_DEPARTMENT = "KEY_DEPARTMENT"
        const val KEY_YEAR = "KEY_YEAR"
        const val KEY_EMAIL = "KEY_EMAIL"
    }

    private lateinit var tvGreeting: TextView
    private lateinit var etStudentId: EditText
    private lateinit var etFullName: EditText
    private lateinit var spinnerDepartment: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var etEmail: EditText
    private lateinit var btnSaveProfile: Button

    private val departments = arrayOf(
        "CSE",
        "EEE",
        "BBA",
        "English",
        "Law"
    )

    private val years = arrayOf(
        "1st Year",
        "2nd Year",
        "3rd Year",
        "4th Year"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvGreeting = findViewById(R.id.tvGreeting)
        etStudentId = findViewById(R.id.etStudentId)
        etFullName = findViewById(R.id.etFullName)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        spinnerYear = findViewById(R.id.spinnerYear)
        etEmail = findViewById(R.id.etEmail)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        spinnerDepartment.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            departments
        )

        spinnerYear.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            years
        )

        loadProfile()

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {

        val prefs = getSharedPreferences(
            PROFILE_PREFS,
            Context.MODE_PRIVATE
        )

        with(prefs.edit()) {

            putString(KEY_STUDENT_ID, etStudentId.text.toString())

            putString(KEY_STUDENT_NAME, etFullName.text.toString())

            putString(
                KEY_DEPARTMENT,
                spinnerDepartment.selectedItem.toString()
            )

            putString(
                KEY_YEAR,
                spinnerYear.selectedItem.toString()
            )

            putString(KEY_EMAIL, etEmail.text.toString())

            apply()
        }

        tvGreeting.text =
            "Welcome back, ${etFullName.text.toString()}!"

        Toast.makeText(
            this,
            "Profile Saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadProfile() {

        val prefs = getSharedPreferences(
            PROFILE_PREFS,
            Context.MODE_PRIVATE
        )

        val name = prefs.getString(KEY_STUDENT_NAME, "")
        val studentId = prefs.getString(KEY_STUDENT_ID, "")
        val department = prefs.getString(KEY_DEPARTMENT, "CSE")
        val year = prefs.getString(KEY_YEAR, "1st Year")
        val email = prefs.getString(KEY_EMAIL, "")

        if (name.isNullOrEmpty()) {
            tvGreeting.text = "Welcome back, Student!"
        } else {
            tvGreeting.text = "Welcome back, $name!"
        }

        etStudentId.setText(studentId)
        etFullName.setText(name)
        etEmail.setText(email)

        spinnerDepartment.setSelection(
            departments.indexOf(department)
        )

        spinnerYear.setSelection(
            years.indexOf(year)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}