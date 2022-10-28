package com.example.basicAddressBook.ui.contactList

import androidx.lifecycle.*
import com.example.basicAddressBook.model.Contact

class ContactListViewModel()  : ViewModel() {

    private val _contactListData = MutableLiveData<List<Contact>?>().apply {
        val emptyList: List<Contact> = emptyList<Contact>()
        value = emptyList
    }
    val contactListData: MutableLiveData<List<Contact>?> = _contactListData

    fun refreshListWithDatabase(currentContactList: List<Contact>?) {
        contactListData.value = currentContactList
    }



}