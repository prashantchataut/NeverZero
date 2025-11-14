package com.productivitystreak.data.local.entity

import androidx.room.TypeConverter
import com.productivitystreak.data.model.StreakDayRecord
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDate
import kotlin.math.max

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val intListType = Types.newParameterizedType(List::class.java, Integer::class.java)
    private val dayRecordListType = Types.newParameterizedType(List::class.java, StreakDayRecord::class.java)
    private val legacyIntAdapter = moshi.adapter<List<Int>>(intListType)
    private val dayRecordAdapter = moshi.adapter<List<StreakDayRecord>>(dayRecordListType)

    @TypeConverter
    fun fromStreakDayRecords(value: List<StreakDayRecord>): String {
        return dayRecordAdapter.toJson(value)
    }

    @TypeConverter
    fun toStreakDayRecords(value: String): List<StreakDayRecord> {
        return runCatching { dayRecordAdapter.fromJson(value) ?: emptyList() }
            .getOrElse {
                val legacy = legacyIntAdapter.fromJson(value) ?: emptyList()
                if (legacy.isEmpty()) return emptyList()
                val today = LocalDate.now()
                legacy.mapIndexed { index, completed ->
                    val offset = (legacy.size - 1 - index).toLong()
                    StreakDayRecord(
                        date = today.minusDays(offset).toString(),
                        completed = completed,
                        goal = max(completed, 1)
                    )
                }
            }
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
