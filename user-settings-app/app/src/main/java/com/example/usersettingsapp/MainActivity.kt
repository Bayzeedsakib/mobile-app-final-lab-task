package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    companion object {
        const val APP_SETTINGS = "AppSettings"

        const val KEY_THEME = "KEY_THEME"
        const val KEY_NOTIFICATIONS = "KEY_NOTIFICATIONS"
        const val KEY_LANGUAGE = "LANGUAGE_KEY"
        const val KEY_FONT_SIZE = "KEY_FONT_SIZE"
        const val KEY_LAST_SAVED = "KEY_LAST_SAVED"
    }

    private lateinit var etStudentName: EditText
    private lateinit var rgTheme: RadioGroup
    private lateinit var rbLight: RadioButton
    private lateinit var rbDark: RadioButton
    private lateinit var rbSystem: RadioButton
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var spinnerLanguage: Spinner
    private lateinit var seekBarFont: SeekBar
    private lateinit var tvFontSize: TextView

    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var btnView: Button

    private lateinit var fabProfile: FloatingActionButton

    private val languages = arrayOf(
        "English",
        "Bangla",
        "Arabic",
        "French"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etStudentName = findViewById(R.id.etStudentName)
        rgTheme = findViewById(R.id.rgTheme)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)

        switchNotifications = findViewById(R.id.switchNotifications)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        seekBarFont = findViewById(R.id.seekBarFont)
        tvFontSize = findViewById(R.id.tvFontSize)

        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        btnView = findViewById(R.id.btnView)

        fabProfile = findViewById(R.id.fabProfile)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )

        spinnerLanguage.adapter = adapter

        seekBarFont.max = 12
        seekBarFont.progress = 4

        updateFontSizeLabel(16)

        seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val fontSize = progress + 12
                updateFontSizeLabel(fontSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSave.setOnClickListener {
            saveSettings()
        }

        btnReset.setOnClickListener {
            resetPreferences()
        }

        btnView.setOnClickListener {
            startActivity(
                Intent(this, SettingsViewerActivity::class.java)
            )
        }

        fabProfile.setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        restoreSettings()
    }

    private fun saveSettings() {

        val selectedTheme = when {
            rbLight.isChecked -> "light"
            rbDark.isChecked -> "dark"
            else -> "system"
        }

        val prefs = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)

        with(prefs.edit()) {

            putString(KEY_THEME, selectedTheme)

            putBoolean(
                KEY_NOTIFICATIONS,
                switchNotifications.isChecked
            )

            putString(
                KEY_LANGUAGE,
                spinnerLanguage.selectedItem.toString()
            )

            putInt(
                KEY_FONT_SIZE,
                seekBarFont.progress + 12
            )

            putLong(
                KEY_LAST_SAVED,
                System.currentTimeMillis()
            )

            apply()
        }

        Toast.makeText(
            this,
            "Settings Saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun restoreSettings() {

        val prefs = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)

        val theme = prefs.getString(KEY_THEME, "light")
        val notifications = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        val language = prefs.getString(KEY_LANGUAGE, "English")
        val fontSize = prefs.getInt(KEY_FONT_SIZE, 16)

        when (theme) {
            "light" -> rbLight.isChecked = true
            "dark" -> rbDark.isChecked = true
            "system" -> rbSystem.isChecked = true
        }

        switchNotifications.isChecked = notifications

        val langPosition = languages.indexOf(language)
        spinnerLanguage.setSelection(langPosition)

        seekBarFont.progress = fontSize - 12

        updateFontSizeLabel(fontSize)
    }

    private fun resetPreferences() {

        val prefs = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)

        prefs.edit().clear().apply()

        etStudentName.setText("")

        rbLight.isChecked = true

        switchNotifications.isChecked = true

        spinnerLanguage.setSelection(0)

        seekBarFont.progress = 4

        updateFontSizeLabel(16)

        Toast.makeText(
            this,
            "Settings Reset to Default",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateFontSizeLabel(size: Int) {
        tvFontSize.text = "Font Size: ${size}sp"
    }
}