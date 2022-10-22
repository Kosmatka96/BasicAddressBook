package com.example.basicaddressbook.contact

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
                   var fax : String) {

    companion object {
        fun getEmptyContact() : Contact {
            return Contact("", "", "", "", "", "", "", "", "", "", "")
        }
    }

}