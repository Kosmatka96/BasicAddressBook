package com.example.basicaddressbook.ui.createContact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.basicaddressbook.model.Contact

class CreateContactViewModel : ViewModel() {
    private val _newContact = MutableLiveData<Contact>().apply {
        value = Contact.getEmptyContact()
    }
    val newContact: MutableLiveData<Contact> = _newContact

    fun updateContact(contact: Contact) {
        newContact.value = contact
    }
}