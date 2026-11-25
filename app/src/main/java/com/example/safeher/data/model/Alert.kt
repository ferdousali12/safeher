package com.example.safeher.data.model

data class Alert(
    val id: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "latitude" to latitude,
        "longitude" to longitude,
        "timestamp" to timestamp,
        "status" to status
    )
}
