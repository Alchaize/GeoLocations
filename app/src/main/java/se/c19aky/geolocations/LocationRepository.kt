package se.c19aky.geolocations

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import se.c19aky.geolocations.database.LocationDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "location-database"

class LocationRepository private constructor(context: Context) {

    private val database: LocationDatabase = Room.databaseBuilder(
        context.applicationContext,
        LocationDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val locationDao = database.locationDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getLocations(): LiveData<List<Location>> = locationDao.getLocations()

    fun getLocation(id: UUID): LiveData<Location?> = locationDao.getLocation(id)

    fun updateLocation(location: Location) {
        executor.execute {
            locationDao.updateLocation(location)
        }
    }

    fun addLocation(location: Location) {
        executor.execute {
            locationDao.addLocation(location)
        }
    }

    companion object {
        private var INSTANCE: LocationRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = LocationRepository(context)
            }
        }

        fun get(): LocationRepository {
            return INSTANCE ?: throw IllegalStateException("LocationRepository must be initialized")
        }
    }

}