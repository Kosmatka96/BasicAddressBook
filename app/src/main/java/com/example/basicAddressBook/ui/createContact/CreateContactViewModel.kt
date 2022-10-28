package com.example.basicAddressBook.ui.createContact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.basicAddressBook.model.Contact

class CreateContactViewModel : ViewModel() {
    private val _newContact = MutableLiveData<Contact>().apply {
        value = Contact.getEmptyContact()
    }
    var newContact: MutableLiveData<Contact> = _newContact

    fun resetContact(contact: Contact) {
        newContact.postValue(contact)
    }
}