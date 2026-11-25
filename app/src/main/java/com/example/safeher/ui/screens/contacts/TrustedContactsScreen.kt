package com.example.safeher.ui.screens.contacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safeher.data.model.Contact
import com.example.safeher.components.SafeHerTextField
import com.example.safeher.presentation.contacts.ContactsViewModel
import com.example.safeher.utils.DemoManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedContactsScreen(
    onBack: () -> Unit,
    viewModel: ContactsViewModel = viewModel()
) {
    val isDemoMode by DemoManager.isDemoMode.collectAsState()
    val realContacts by viewModel.contacts.collectAsState()
    val demoContacts by DemoManager.demoContacts.collectAsState()

    val contacts = if (isDemoMode) demoContacts else realContacts
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var isPreferred by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isDemoMode) "Trusted Contacts (Demo)" else "Trusted Contacts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingContact = null
                name = ""
                phone = ""
                relationship = ""
                isPreferred = false
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (errorMessage != null && !isDemoMode) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (contacts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No emergency contacts added yet.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(contacts) { contact ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            if (contact.isPreferred) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Badge { Text("Preferred") }
                                            }
                                        }
                                        Text(text = contact.phoneNumber, fontSize = 14.sp)
                                        Text(text = contact.relationship, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        editingContact = contact
                                        name = contact.name
                                        phone = contact.phoneNumber
                                        relationship = contact.relationship
                                        isPreferred = contact.isPreferred
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        if (isDemoMode) {
                                            DemoManager.deleteDemoContact(contact.id)
                                        } else {
                                            viewModel.deleteContact(contact.id)
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(if (editingContact == null) "Add Contact" else "Edit Contact") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SafeHerTextField(value = name, onValueChange = { name = it }, label = "Name")
                            SafeHerTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number")
                            SafeHerTextField(value = relationship, onValueChange = { relationship = it }, label = "Relationship")
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(checked = isPreferred, onCheckedChange = { isPreferred = it })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Preferred Emergency Contact")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (isDemoMode) {
                                val contact = Contact(
                                    userId = DemoManager.demoUser.uid,
                                    name = name,
                                    phoneNumber = phone,
                                    relationship = relationship,
                                    isPreferred = isPreferred
                                )
                                DemoManager.addDemoContact(contact)
                                showDialog = false
                            } else {
                                if (editingContact == null) {
                                    viewModel.addContact(name, phone, relationship, isPreferred) {
                                        showDialog = false
                                    }
                                } else {
                                    val updated = editingContact!!.copy(
                                        name = name,
                                        phoneNumber = phone,
                                        relationship = relationship,
                                        isPreferred = isPreferred
                                    )
                                    viewModel.updateContact(updated) {
                                        showDialog = false
                                    }
                                }
                            }
                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
