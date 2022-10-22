package com.example.basicaddressbook.ui.contactList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "There are no Contacts, import to add more"
    }
    val text: LiveData<String> = _text
}