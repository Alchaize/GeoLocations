package se.c19aky.geolocations.database

import androidx.room.TypeConverter
import java.util.*

/**
 * Type converter, needed for converting between UUID and String
 */
class LocationTypeConverter {

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}