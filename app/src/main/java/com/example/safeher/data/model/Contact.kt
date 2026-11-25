package com.example.safeher.data.model

data class Contact(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val relationship: String = "",
    val isPreferred: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "name" to name,
        "phoneNumber" to phoneNumber,
        "relationship" to relationship,
        "isPreferred" to isPreferred
    )
}
