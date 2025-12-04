package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tracks daily completion status for protocols.
 * Used for streak calculation by checking consecutive completed dates.
 *
 * @property id Auto-generated primary key
 * @property date ISO date format (e.g., "2025-12-04")
 * @property protocolId Foreign key to the associated protocol
 * @property status Current completion status
 * @property completedAt Timestamp when marked complete (null if not complete)
 */
@Entity(
    tableName = "daily_logs",
    foreignKeys = [
        ForeignKey(
            entity = ProtocolEntity::class,
            parentColumns = ["id"],
            childColumns = ["protocolId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["protocolId"]),
        Index(value = ["protocolId", "date"], unique = true)
    ]
)
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val protocolId: String,
    val status: LogStatus,
    val completedAt: Long? = null
)

/**
 * Status of a daily log entry.
 */
enum class LogStatus {
    /** Not yet acted upon */
    PENDING,
    /** Successfully completed */
    COMPLETE,
    /** Intentionally skipped */
    SKIPPED
}

// --- Domain Mapping Extensions ---

data class DailyLog(
    val id: Long,
    val date: String,
    val protocolId: String,
    val status: LogStatus,
    val completedAt: Long?
)

fun DailyLogEntity.toDailyLog(): DailyLog = DailyLog(
    id = id,
    date = date,
    protocolId = protocolId,
    status = status,
    completedAt = completedAt
)

fun DailyLog.toEntity(): DailyLogEntity = DailyLogEntity(
    id = id,
    date = date,
    protocolId = protocolId,
    status = status,
    completedAt = completedAt
)
