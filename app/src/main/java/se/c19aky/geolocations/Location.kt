package se.c19aky.geolocations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Model class representing a location
 */
@Entity
data class Location(@PrimaryKey val id: UUID = UUID.randomUUID(),
                    @ColumnInfo var name: String = "Location",
                    @ColumnInfo var latitude: Double = 0.0,
                    @ColumnInfo var longitude: Double = 0.0,
                    @ColumnInfo var description: String = "Description")