package com.productivitystreak.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.productivitystreak.data.local.entity.ProtocolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocolDao {

    @Query("SELECT * FROM protocols WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActiveProtocols(): Flow<List<ProtocolEntity>>

    @Query("SELECT * FROM protocols ORDER BY createdAt DESC")
    fun observeAllProtocols(): Flow<List<ProtocolEntity>>

    @Query("SELECT * FROM protocols WHERE id = :id")
    suspend fun getById(id: String): ProtocolEntity?

    @Query("SELECT * FROM protocols WHERE id = :id")
    fun observeById(id: String): Flow<ProtocolEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(protocol: ProtocolEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocols: List<ProtocolEntity>)

    @Update
    suspend fun update(protocol: ProtocolEntity)

    @Delete
    suspend fun delete(protocol: ProtocolEntity)

    @Query("DELETE FROM protocols WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE protocols SET isActive = :isActive WHERE id = :id")
    suspend fun setActive(id: String, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM protocols WHERE isActive = 1")
    suspend fun getActiveCount(): Int

    @Query("DELETE FROM protocols")
    suspend fun deleteAll()
}
