package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_capsules")
data class TimeCapsuleEntity(
    @PrimaryKey
    val id: String,
    val message: String,
    val creationDateMillis: Long,
    val deliveryDateMillis: Long,
    val goalDescription: String,
    val opened: Boolean = false,
    val reflection: String? = null,
    val openedAtMillis: Long? = null
)
