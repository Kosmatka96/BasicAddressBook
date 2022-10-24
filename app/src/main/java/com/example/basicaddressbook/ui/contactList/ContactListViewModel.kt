package com.example.basicaddressbook.ui.contactList

import androidx.lifecycle.*
import com.example.basicaddressbook.model.Contact

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