package com.rohanNarayan.omnicrosswords.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String>? {
        val listType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListOfStringMaps(value: List<Map<String, String>>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListOfStringMaps(value: String): List<Map<String, String>>? {
        val listType = object : TypeToken <List<Map<String, String>>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromMapOfStringLists(value: Map<String, List<Int>>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMapOfStringLists(value: String): Map<String, List<Int>>? {
        val listType = object : TypeToken <Map<String, List<Int>>>() {}.type
        return Gson().fromJson(value, listType)
    }


}