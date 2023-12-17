package com.example.locationfetcher_kotlin
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.locationfetcher_kotlin.LocationViewModel
import com.example.locationfetcher_kotlin.MainActivity
import com.example.locationfetcher_kotlin.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private var locationViewModel: LocationViewModel? = null
    private var lastKnownLocation: Location? = null
    private var savedLocation: Location? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        //If the view model has a location we update
        //If the view model has a location we update
        //If the view model has a location we update
        if (locationViewModel?.getLastKnownLocation() != null) {
            lastKnownLocation = locationViewModel?.getLastKnownLocation()
            updateLocationOnScreen(lastKnownLocation)
        }

        // Request location permissions
        requestLocationPermissions()

        // Initialize fusedLocationClient
        fusedLocationClient = FusedLocationProviderClient(this)

        // Initialize locationRequest with your desired settings
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000) // Update every minute
            .setFastestInterval(5000)

        // Initialize locationCallback to receive location updates

        // Initialize locationCallback to receive location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d("LocationActivity", "onLocationResultCalled")
                if (locationResult != null) {
                    lastKnownLocation = locationResult.lastLocation
                    locationViewModel!!.setLastKnownLocation(lastKnownLocation)
                    Log.d(
                        "LocationActivity",
                        "Location received: " + lastKnownLocation!!.getLatitude() + ", " + lastKnownLocation!!.getLongitude()
                    ) // Update the global variable
                    updateLocationOnScreen(lastKnownLocation)
                }
            }
        }

        // Views
      //  val backButton = findViewById<Button>(R.id.location_activity_back_button)

        val backButton = binding.locationActivityBackButton

        val locationDataTextView = binding.locationDataTextView
        val startCaptureButton = binding.startCaptureButton
        val stopCaptureButton = binding.stopCaptureButton
        val saveCaptureButton = binding.saveCaptureButton
        // Actions
        backButton.setOnClickListener {
            stopLocationUpdates()
            // Use an Intent to navigate back to HomeActivity (or HomeFragment)
            val intent = Intent(
                this@LocationActivity,
                MainActivity::class.java
            )
            if (savedLocation != null) {
                intent.putExtra("latitude", savedLocation!!.latitude)
                intent.putExtra("longitude", savedLocation!!.longitude)
                savedLocation = locationViewModel?.getLastKnownLocation()
            }
            startActivity(intent)
            finish() // Optional: Finish the LocationActivity to remove it from the back stack
        }

        // Start capturing location when the "Start Capture" button is pressed
        startCaptureButton.setOnClickListener { startLocationUpdates() }

        // Stop capturing location when the "Stop Capture" button is pressed
        stopCaptureButton.setOnClickListener { stopLocationUpdates() }

        // Save the current latitude and longitude when the "Save" button is pressed
        saveCaptureButton.setOnClickListener {
            if (lastKnownLocation != null) {
                savedLocation = lastKnownLocation
            }
            // Implement saving logic here
            Toast.makeText(this@LocationActivity, "Location saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    private fun startLocationUpdates() {
        if (fusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Toast.makeText(this, "Location updates started", Toast.LENGTH_SHORT).show()
        } else {
            Log.e("LocationActivity", "FusedLocationProviderClient is null")
        }
    }


    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        Toast.makeText(this, "Location updates stopped", Toast.LENGTH_SHORT).show()
    }

    private fun updateLocationOnScreen(location: Location?) {
        runOnUiThread {
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                binding.locationDataTextView.text = String.format(
                    "Latitude: %s\nLongitude: %s",
                    latitude,
                    longitude
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start capturing location
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
    }
}
