package se.c19aky.geolocations.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import se.c19aky.geolocations.Location

@Database(entities = [ Location::class ], version = 1)
@TypeConverters(LocationTypeConverter::class)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

}