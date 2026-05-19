package com.example.usersettingsapp

import com.example.usersettingsapp.R

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SettingsViewerActivity : AppCompatActivity() {

    private lateinit var layoutContent: LinearLayout
    private lateinit var tvEmpty: TextView

    private lateinit var tvTheme: TextView
    private lateinit var tvNotifications: TextView
    private lateinit var tvLanguage: TextView
    private lateinit var tvFontSize: TextView
    private lateinit var tvLastSaved: TextView

    private lateinit var btnEdit: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_viewer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layoutContent = findViewById(R.id.layoutContent)
        tvEmpty = findViewById(R.id.tvEmpty)

        tvTheme = findViewById(R.id.tvTheme)
        tvNotifications = findViewById(R.id.tvNotifications)
        tvLanguage = findViewById(R.id.tvLanguage)
        tvFontSize = findViewById(R.id.tvFontSize)
        tvLastSaved = findViewById(R.id.tvLastSaved)

        btnEdit = findViewById(R.id.btnEdit)
        btnBack = findViewById(R.id.btnBack)

        loadSettings()

        btnEdit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadSettings() {

        val prefs = getSharedPreferences(
            MainActivity.APP_SETTINGS,
            Context.MODE_PRIVATE
        )

        if (!prefs.contains(MainActivity.KEY_LAST_SAVED)) {

            layoutContent.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE

            return
        }

        layoutContent.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        val theme = prefs.getString(
            MainActivity.KEY_THEME,
            "light"
        )

        val notifications = prefs.getBoolean(
            MainActivity.KEY_NOTIFICATIONS,
            true
        )

        val language = prefs.getString(
            MainActivity.KEY_LANGUAGE,
            "English"
        )

        val fontSize = prefs.getInt(
            MainActivity.KEY_FONT_SIZE,
            16
        )

        val lastSaved = prefs.getLong(
            MainActivity.KEY_LAST_SAVED,
            0
        )

        val formattedDate = SimpleDateFormat(
            "dd MMM yyyy hh:mm a",
            Locale.getDefault()
        ).format(Date(lastSaved))

        tvTheme.text = theme
        tvNotifications.text = notifications.toString()
        tvLanguage.text = language
        tvFontSize.text = "${fontSize}sp"
        tvLastSaved.text = formattedDate
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}