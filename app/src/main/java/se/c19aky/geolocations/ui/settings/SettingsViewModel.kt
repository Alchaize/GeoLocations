package se.c19aky.geolocations.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.LocationRepository

class SettingsViewModel  : ViewModel() {

    private val locationRepository = LocationRepository.get()
    val locationListLiveData = locationRepository.getLocations()

    fun deleteLocations(locations: List<Location>) {
        if (locations.isNotEmpty()) {
            for (location in locations)
                locationRepository.deleteLocation(location)
        }
    }
}