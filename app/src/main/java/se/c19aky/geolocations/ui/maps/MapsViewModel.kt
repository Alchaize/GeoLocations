package se.c19aky.geolocations.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.LocationRepository

class MapsViewModel : ViewModel() {

    private val locationRepository = LocationRepository.get()
    val locationListLiveData = locationRepository.getLocations()

    val currentLocation: MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    fun addLocation(location: Location) {
        locationRepository.addLocation(location)
    }
}