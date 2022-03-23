package se.c19aky.geolocations.ui.list

import androidx.lifecycle.ViewModel
import se.c19aky.geolocations.LocationRepository

class ListViewModel : ViewModel() {

    private val locationRepository = LocationRepository.get()
    val locationListLiveData = locationRepository.getLocations()

}