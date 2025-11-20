package com.productivitystreak.data.model

/**
 * Domain model for the Time Capsule protocol.
 */
data class TimeCapsule(
    val id: String,
    val message: String,
    val creationDateMillis: Long,
    val deliveryDateMillis: Long,
    val goalDescription: String,
    val opened: Boolean,
    val reflection: String?
)
