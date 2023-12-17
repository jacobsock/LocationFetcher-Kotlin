package com.example.locationfetcher_kotlin

import android.location.Location
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private var lastKnownLocation: Location? = null

    fun setLastKnownLocation(location: Location?) {
        lastKnownLocation = location
    }

    fun getLastKnownLocation(): Location? {
        return lastKnownLocation
    }
}
