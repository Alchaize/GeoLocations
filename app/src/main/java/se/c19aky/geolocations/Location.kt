package se.c19aky.geolocations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Location(@PrimaryKey val id: UUID = UUID.randomUUID(),
                    @ColumnInfo var name: String = "Location",
                    @ColumnInfo var latitude: Int = 0,
                    @ColumnInfo var longitude: Int = 0)