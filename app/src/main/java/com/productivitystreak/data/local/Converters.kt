package com.productivitystreak.data.local

import androidx.room.TypeConverter
import com.productivitystreak.data.local.entity.LogStatus
import com.productivitystreak.data.local.entity.ProtocolFrequency
import com.productivitystreak.data.local.entity.ProtocolType
import com.productivitystreak.data.model.HabitAttribute
import com.productivitystreak.data.model.StreakDayRecord
import com.productivitystreak.data.model.StreakDifficulty
import com.productivitystreak.data.model.StreakFrequency
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val streakDayRecordListType = Types.newParameterizedType(List::class.java, StreakDayRecord::class.java)
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)

    @TypeConverter
    fun fromStreakDayRecordList(list: List<StreakDayRecord>): String {
        return moshi.adapter<List<StreakDayRecord>>(streakDayRecordListType).toJson(list)
    }

    @TypeConverter
    fun toStreakDayRecordList(json: String): List<StreakDayRecord> {
        return moshi.adapter<List<StreakDayRecord>>(streakDayRecordListType).fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return moshi.adapter<List<String>>(stringListType).toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        return moshi.adapter<List<String>>(stringListType).fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun fromStreakFrequency(frequency: StreakFrequency): String {
        return frequency.name
    }

    @TypeConverter
    fun toStreakFrequency(name: String): StreakFrequency {
        return try {
            StreakFrequency.valueOf(name)
        } catch (e: IllegalArgumentException) {
            StreakFrequency.DAILY // Default fallback
        }
    }

    @TypeConverter
    fun fromStreakDifficulty(difficulty: StreakDifficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toStreakDifficulty(name: String): StreakDifficulty {
        return try {
            StreakDifficulty.valueOf(name)
        } catch (e: IllegalArgumentException) {
            StreakDifficulty.BALANCED // Default fallback
        }
    }

    // --- Protocol Entity Converters ---

    @TypeConverter
    fun fromProtocolType(type: ProtocolType): String = type.name

    @TypeConverter
    fun toProtocolType(name: String): ProtocolType {
        return try {
            ProtocolType.valueOf(name)
        } catch (e: IllegalArgumentException) {
            ProtocolType.DAILY
        }
    }

    @TypeConverter
    fun fromProtocolFrequency(frequency: ProtocolFrequency): String = frequency.name

    @TypeConverter
    fun toProtocolFrequency(name: String): ProtocolFrequency {
        return try {
            ProtocolFrequency.valueOf(name)
        } catch (e: IllegalArgumentException) {
            ProtocolFrequency.DAILY
        }
    }

    @TypeConverter
    fun fromLogStatus(status: LogStatus): String = status.name

    @TypeConverter
    fun toLogStatus(name: String): LogStatus {
        return try {
            LogStatus.valueOf(name)
        } catch (e: IllegalArgumentException) {
            LogStatus.PENDING
        }
    }

    @TypeConverter
    fun fromHabitAttribute(attribute: HabitAttribute): String = attribute.name

    @TypeConverter
    fun toHabitAttribute(name: String): HabitAttribute {
        return try {
            HabitAttribute.valueOf(name)
        } catch (e: IllegalArgumentException) {
            HabitAttribute.NONE
        }
    }
}

