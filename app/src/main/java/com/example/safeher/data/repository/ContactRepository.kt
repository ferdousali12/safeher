package com.example.safeher.data.repository

import com.example.safeher.data.model.Contact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val contactsCollection = firestore.collection("contacts")

    suspend fun addContact(contact: Contact): Result<String> {
        return try {
            val snapshot = contactsCollection
                .whereEqualTo("userId", contact.userId)
                .whereEqualTo("phoneNumber", contact.phoneNumber)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                return Result.failure(Exception("Contact with this phone number already exists."))
            }

            val docRef = contactsCollection.document()
            val newContact = contact.copy(id = docRef.id)
            docRef.set(newContact.toMap()).await()
            Result.success(newContact.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContact(contact: Contact): Result<Unit> {
        return try {
            contactsCollection.document(contact.id).set(contact.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getContacts(userId: String): Result<List<Contact>> {
        return try {
            val snapshot = contactsCollection.whereEqualTo("userId", userId).get().await()
            val contacts = snapshot.documents.mapNotNull { it.toObject(Contact::class.java) }
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContact(contactId: String): Result<Unit> {
        return try {
            contactsCollection.document(contactId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
