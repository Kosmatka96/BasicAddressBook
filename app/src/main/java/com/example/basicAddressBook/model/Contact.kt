package com.example.basicAddressBook.model
import java.io.Serializable;

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
                   var groupId: Int,
                   var selected: Int,) : Serializable {

    companion object {
        fun getEmptyContact() : Contact {
            return Contact("", "", "", "", "", "", "",
                "", "", "", "", 0, 0)
        }

        fun isValidContact(c: Contact?) : Boolean {
            if (c != null && c.customerId.isNotEmpty())
                return true
            return false
        }

        fun getDisplayId(c: Contact?): String {
            // Helps display contact identification within table (even with duplicates)
            if (isValidContact(c)) {
                var groupStr = ""
                val rowId = c!!.groupId
                if(rowId > 0) groupStr = " (#$rowId)"
                return "${c.customerId}$groupStr"
            }
            return ""
        }
    }

}