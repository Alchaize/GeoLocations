package se.c19aky.geolocations

import android.app.Application

class GeoLocationApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationRepository.initialize(this)
    }
}