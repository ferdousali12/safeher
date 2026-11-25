package com.example.safeher.data.model

data class Journey(
    val id: String = "",
    val userId: String = "",
    val destination: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val expectedDurationMinutes: Int = 30,
    val status: String = "IN_PROGRESS"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "destination" to destination,
        "startTime" to startTime,
        "expectedDurationMinutes" to expectedDurationMinutes,
        "status" to status
    )
}
