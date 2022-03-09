package se.c19aky.geolocations.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import se.c19aky.geolocations.Location
import se.c19aky.geolocations.LocationRepository
import java.util.*

class LocationViewModel : ViewModel() {

    private val locationRepository = LocationRepository.get()
    private val locationIdLiveData = MutableLiveData<UUID>()

    var locationLiveData: LiveData<Location?> =
        Transformations.switchMap(locationIdLiveData) { locationId ->
            locationRepository.getLocation(locationId)
        }

    fun loadLocation(locationId: UUID) {
        locationIdLiveData.value = locationId
    }

    fun saveLocation(location: Location) {
        locationRepository.updateLocation(location)
    }

}