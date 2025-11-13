package com.productivitystreak.data.local.entity

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        val type = Types.newParameterizedType(List::class.java, Integer::class.java)
        val adapter = moshi.adapter<List<Int>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val type = Types.newParameterizedType(List::class.java, Integer::class.java)
        val adapter = moshi.adapter<List<Int>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }
}
