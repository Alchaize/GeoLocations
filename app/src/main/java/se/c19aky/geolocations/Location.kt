package se.c19aky.geolocations

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Location(@PrimaryKey val id: UUID = UUID.randomUUID(), var name: String = "Location")