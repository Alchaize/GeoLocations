package se.c19aky.geolocations.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.LocationRepository

class MapsViewModel : ViewModel() {

    var locationPermissionGiven = false

    private val locationRepository = LocationRepository.get()
    val locationListLiveData = locationRepository.getLocations()

    val locations: MutableList<Location> = mutableListOf()

    val currentLocation: MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    val redrawMarkers: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun addLocation(location: Location) {
        locationRepository.addLocation(location)
    }
}