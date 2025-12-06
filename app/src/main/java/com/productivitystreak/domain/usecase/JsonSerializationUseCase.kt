package com.productivitystreak.domain.usecase

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Shared use case for JSON serialization with Moshi.
 * Eliminates repeated adapter creation across ViewModels.
 */
class JsonSerializationUseCase(private val moshi: Moshi) {

    inline fun <reified T> serializeList(list: List<T>): String {
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter = moshi.adapter<List<T>>(type)
        return adapter.toJson(list)
    }

    inline fun <reified T> deserializeList(json: String): List<T>? {
        return try {
            val type = Types.newParameterizedType(List::class.java, T::class.java)
            val adapter = moshi.adapter<List<T>>(type)
            adapter.fromJson(json)
        } catch (e: Exception) {
            Log.e("JsonSerializationUseCase", "Error deserializing list", e)
            null
        }
    }

    inline fun <reified T> serialize(item: T): String {
        val adapter = moshi.adapter(T::class.java)
        return adapter.toJson(item)
    }

    inline fun <reified T> deserialize(json: String): T? {
        return try {
            val adapter = moshi.adapter(T::class.java)
            adapter.fromJson(json)
        } catch (e: Exception) {
            Log.e("JsonSerializationUseCase", "Error deserializing item", e)
            null
        }
    }
}
