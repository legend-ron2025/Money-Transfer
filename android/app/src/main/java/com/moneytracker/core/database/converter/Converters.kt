package com.moneytracker.core.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null || value.isEmpty()) return emptyList()
        val type: Type = TypeToken.getParameterized(List::class.java, String::class.java).type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        if (list == null || list.isEmpty()) return null
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String> {
        if (value == null || value.isEmpty()) return emptyMap()
        val type: Type = TypeToken.getParameterized(Map::class.java, String::class.java, String::class.java).type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toStringMap(map: Map<String, String>?): String? {
        if (map == null || map.isEmpty()) return null
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromDate(value: String?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toDate(date: Date?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}