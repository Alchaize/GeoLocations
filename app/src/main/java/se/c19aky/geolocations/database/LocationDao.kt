package se.c19aky.geolocations.database

import androidx.lifecycle.LiveData
import androidx.room.*
import se.c19aky.geolocations.Location
import java.util.*

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getLocations(): LiveData<List<Location>>

    @Query("SELECT * FROM location WHERE id=(:id)")
    fun getLocation(id: UUID): LiveData<Location?>

    @Update
    fun updateLocation(location: Location)

    @Insert
    fun addLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)
}