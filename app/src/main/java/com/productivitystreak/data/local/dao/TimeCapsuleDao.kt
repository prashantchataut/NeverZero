package com.productivitystreak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.productivitystreak.data.local.entity.TimeCapsuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeCapsuleDao {

    @Query("SELECT * FROM time_capsules ORDER BY deliveryDateMillis ASC")
    fun observeAllCapsules(): Flow<List<TimeCapsuleEntity>>

    @Query("SELECT * FROM time_capsules WHERE id = :id")
    suspend fun getById(id: String): TimeCapsuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: TimeCapsuleEntity)

    @Update
    suspend fun updateCapsule(capsule: TimeCapsuleEntity)

    @Query("SELECT * FROM time_capsules WHERE opened = 0 AND deliveryDateMillis <= :nowMillis")
    suspend fun getDueCapsules(nowMillis: Long): List<TimeCapsuleEntity>
}
