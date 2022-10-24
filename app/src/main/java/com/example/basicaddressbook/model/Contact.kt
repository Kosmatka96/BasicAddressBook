package com.example.basicaddressbook.model

// model for the data type to be stored

data class Contact(var customerId : String,
                   var companyName : String,
                   var contactName : String,
                   var contactTitle : String,
                   var address : String,
                   var city : String,
                   var email : String,
                   var postalCode : String,
                   var country : String,
                   var phone : String,
                   var fax : String,
                   var selected: Int) {

    companion object {
        fun getEmptyContact() : Contact {
            return Contact("", "", "", "", "", "", "",
                "", "", "", "", 0)
        }
    }

}