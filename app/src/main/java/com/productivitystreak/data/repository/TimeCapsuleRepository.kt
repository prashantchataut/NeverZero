package com.productivitystreak.data.repository

import android.database.sqlite.SQLiteException
import com.productivitystreak.data.local.dao.TimeCapsuleDao
import com.productivitystreak.data.local.entity.TimeCapsuleEntity
import com.productivitystreak.data.model.TimeCapsule
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimeCapsuleRepository(private val dao: TimeCapsuleDao) {

    fun observeTimeCapsules(): Flow<List<TimeCapsule>> =
        dao.observeAllCapsules().map { list -> list.map { it.toDomain() } }

    suspend fun createTimeCapsule(
        message: String,
        deliveryDateMillis: Long,
        goalDescription: String
    ): RepositoryResult<String> {
        return try {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val entity = TimeCapsuleEntity(
                id = id,
                message = message,
                creationDateMillis = now,
                deliveryDateMillis = deliveryDateMillis,
                goalDescription = goalDescription
            )
            dao.insertCapsule(entity)
            RepositoryResult.Success(id)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun getTimeCapsule(id: String): TimeCapsule? =
        dao.getById(id)?.toDomain()

    suspend fun getDueCapsules(nowMillis: Long): List<TimeCapsule> =
        dao.getDueCapsules(nowMillis).map { it.toDomain() }

    suspend fun saveReflection(id: String, reflection: String): RepositoryResult<Unit> {
        return try {
            val existing = dao.getById(id) 
                ?: return RepositoryResult.DbError(IllegalStateException("Time capsule not found"))
            val updated = existing.copy(
                opened = true,
                reflection = reflection,
                openedAtMillis = System.currentTimeMillis()
            )
            dao.updateCapsule(updated)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    private fun TimeCapsuleEntity.toDomain(): TimeCapsule =
        TimeCapsule(
            id = id,
            message = message,
            creationDateMillis = creationDateMillis,
            deliveryDateMillis = deliveryDateMillis,
            goalDescription = goalDescription,
            opened = opened,
            reflection = reflection
        )
}
