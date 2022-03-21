package se.c19aky.geolocations.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.LocationRepository

class DashboardViewModel : ViewModel() {

    private val locationRepository = LocationRepository.get()
    val locationListLiveData = locationRepository.getLocations()

}