package com.example.locationfetcher_kotlin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.locationfetcher_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var placeholderTextView: TextView
    private lateinit var placeHolderTextVisibility: String

    private val PREF_NAME = "MyPrefs"
    private val KEY_PLACEHOLDER_TEXT = "placeholder_text"
    private val KEY_PLACEHOLDER_TEXT_VISIBILITY = "placeholder_text_visibility"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        placeHolderTextVisibility =
            preferences.getString(KEY_PLACEHOLDER_TEXT_VISIBILITY, "true").toString()

        // Declare Views
        placeholderTextView = findViewById(R.id.placeholder_textView)

        // Restore text from SharedPreferences
        val placeholderText =
            preferences.getString(KEY_PLACEHOLDER_TEXT, "No location saved").toString()
        placeholderTextView.text = placeholderText

        val locationActivityButton = findViewById<Button>(R.id.location_activity_button)

        // Define Clicks
        locationActivityButton.setOnClickListener {
            val i = Intent(this@MainActivity, LocationActivity::class.java)
            startActivity(i)
        }

        // Check if there are extras in the intent and update placeholderTextView
        val intent = intent

        // If we are coming from the LocationActivity and a Location was saved
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)

            // Check if both latitude and longitude are not null
            if (latitude != 0.0 && longitude != 0.0) {
                // Update placeholderTextView with the received data
                placeholderTextView.text =
                    String.format("Saved Location : \n Latitude: %s\nLongitude: %s", latitude, longitude)

                // Save text to SharedPreferences
                val editor = preferences.edit()
                editor.putString(KEY_PLACEHOLDER_TEXT, placeholderTextView.text.toString())
                // show text
                placeholderTextView.visibility = View.VISIBLE
                editor.putString(KEY_PLACEHOLDER_TEXT_VISIBILITY, "true")

                editor.apply()
            }
        }
    }
}
