package com.productivitystreak.data.repository

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
    ): String {
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
        return id
    }

    suspend fun getTimeCapsule(id: String): TimeCapsule? =
        dao.getById(id)?.toDomain()

    suspend fun getDueCapsules(nowMillis: Long): List<TimeCapsule> =
        dao.getDueCapsules(nowMillis).map { it.toDomain() }

    suspend fun saveReflection(id: String, reflection: String) {
        val existing = dao.getById(id) ?: return
        val updated = existing.copy(
            opened = true,
            reflection = reflection,
            openedAtMillis = System.currentTimeMillis()
        )
        dao.updateCapsule(updated)
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
