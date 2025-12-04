package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.productivitystreak.data.model.HabitAttribute

/**
 * Represents an Identity Protocol - a habit linked to who the user wants to become.
 *
 * @property id Unique identifier for the protocol
 * @property name Display name (e.g., "Read 30 mins daily")
 * @property type Category of protocol behavior
 * @property icon Material icon name for display
 * @property frequency How often the protocol should be completed
 * @property linkedAttribute Which RPG stat this protocol affects
 * @property createdAt Timestamp of creation
 * @property isActive Whether the protocol is currently tracked
 */
@Entity(tableName = "protocols")
data class ProtocolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: ProtocolType,
    val icon: String,
    val frequency: ProtocolFrequency,
    val linkedAttribute: HabitAttribute,
    val createdAt: Long,
    val isActive: Boolean
)

/**
 * Type of protocol based on identity behavior modeling.
 */
enum class ProtocolType {
    /** Core identity habit (e.g., "I am a reader") */
    IDENTITY,
    /** Regular daily practice */
    DAILY,
    /** Weekly commitment */
    WEEKLY
}

/**
 * Frequency at which the protocol should be completed.
 */
enum class ProtocolFrequency {
    DAILY,
    WEEKLY,
    CUSTOM
}

// --- Domain Mapping Extensions ---

data class Protocol(
    val id: String,
    val name: String,
    val type: ProtocolType,
    val icon: String,
    val frequency: ProtocolFrequency,
    val linkedAttribute: HabitAttribute,
    val createdAt: Long,
    val isActive: Boolean
)

fun ProtocolEntity.toProtocol(): Protocol = Protocol(
    id = id,
    name = name,
    type = type,
    icon = icon,
    frequency = frequency,
    linkedAttribute = linkedAttribute,
    createdAt = createdAt,
    isActive = isActive
)

fun Protocol.toEntity(): ProtocolEntity = ProtocolEntity(
    id = id,
    name = name,
    type = type,
    icon = icon,
    frequency = frequency,
    linkedAttribute = linkedAttribute,
    createdAt = createdAt,
    isActive = isActive
)
