package se.c19aky.geolocations.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
}