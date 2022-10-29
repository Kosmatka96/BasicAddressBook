package com.example.basicAddressBook.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.util.Log
import com.example.basicAddressBook.model.Contact


// Database object that handles the table of contacts, defines data table and
// provides an interface for changing/retrieving data

class ContactListTable(context: Context) : AbstractTable(context) {

    companion object{

        const val TABLE_NAME = "contact_list_table"
        const val CUSTOMER_ID_COL = "CustomerID"
        const val COMPANY_NAME_COL = "CompanyName"
        const val CONTACT_NAME_COL = "ContactName"
        const val CONTACT_TITLE_COL = "ContactTitle"
        const val ADDRESS_COL = "Address"
        const val CITY_COL = "City"
        const val EMAIL_COL = "Email"
        const val POSTAL_CODE_COL = "PostalCode"
        const val COUNTRY_COL = "Country"
        const val PHONE_COL = "Phone"
        const val FAX_COL = "Fax"
        const val SELECTED_COL = "Selected"

        const val createTableQueryStr = ("CREATE TABLE " + TABLE_NAME + " (" +
                ROW_ID +            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CUSTOMER_ID_COL +   " TEXT," +
                COMPANY_NAME_COL +  " TEXT," +
                CONTACT_NAME_COL +  " TEXT," +
                CONTACT_TITLE_COL + " TEXT," +
                ADDRESS_COL +       " TEXT," +
                CITY_COL +          " TEXT," +
                EMAIL_COL +         " TEXT," +
                POSTAL_CODE_COL +   " TEXT," +
                COUNTRY_COL +       " TEXT," +
                PHONE_COL +         " TEXT," +
                FAX_COL +           " TEXT," +
                SELECTED_COL +      " INTEGER)")

        // Singleton pattern, implemented in a multi-threaded safe way
        @Volatile private var INSTANCE: ContactListTable? = null
        fun getInstance(context: Context): ContactListTable {
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = ContactListTable(context)
                    INSTANCE = instance
                }
                return instance
            }
        }

        private fun getContentValues(contact: Contact) : ContentValues {
            // add each property of Contact in the form of a key value pair to insert into table
            val values = ContentValues()
            values.put(CUSTOMER_ID_COL, contact.customerId)
            values.put(COMPANY_NAME_COL, contact.companyName)
            values.put(CONTACT_NAME_COL, contact.contactName)
            values.put(CONTACT_TITLE_COL, contact.contactTitle)
            values.put(ADDRESS_COL, contact.address)
            values.put(CITY_COL, contact.city)
            values.put(EMAIL_COL, contact.email)
            values.put(POSTAL_CODE_COL, contact.postalCode)
            values.put(COUNTRY_COL, contact.country)
            values.put(PHONE_COL, contact.phone)
            values.put(FAX_COL, contact.fax)
            values.put(SELECTED_COL, contact.selected)
            return values
        }
    }

    override var tableName = TABLE_NAME
    override var keys = arrayOf(ROW_ID, CUSTOMER_ID_COL, COMPANY_NAME_COL, CONTACT_NAME_COL, CONTACT_TITLE_COL, ADDRESS_COL, CITY_COL,
        EMAIL_COL, POSTAL_CODE_COL, COUNTRY_COL, PHONE_COL, FAX_COL, SELECTED_COL)


    fun getAllContacts(sortBy: String?): List<Contact>? {

        // build orderBy clause based on passed sorting
        var orderBy: String? = null
        if (!sortBy.isNullOrBlank()) {
            orderBy = "$sortBy ASC"
        }

        // get all data from our database
        val cursor =  getCursorFromDatabase(null, orderBy)
        if (cursor != null && cursor.moveToFirst()) {
            val listAsObjects = getCursorListAsObjects(cursor)
            cursor.close()
            return listAsObjects
        }
        return null
    }

    fun addListOfContacts(newListOfContacts: List<Contact>?) {
        // add the list of incoming Contact objects to the database
        if ((newListOfContacts != null) && newListOfContacts.isNotEmpty()) {
            newListOfContacts.forEach {
                addOrUpdateContact(it)
            }
        }
    }

    fun addOrUpdateContact(contact: Contact) : Boolean {
        // checks for valid contact, then adds it if it does not exists, or updates the existing one
        if (Contact.isValidContact(contact)) {
            // add into database
            val select = "$CUSTOMER_ID_COL=${DatabaseUtils.sqlEscapeString(contact.customerId)}"
            insertOrUpdate(select, getContentValues(contact))
            return true
        }
        else {
            Log.e(tableName, "[ERROR]: Unable to add contact to table. Invalid Contact!\n")
        }
        return false
    }

    fun insertDuplicateContact(contact: Contact) : Boolean {
        if (Contact.isValidContact(contact)) {
            forceInsert(getContentValues(contact))
            return true
        }
        else {
            Log.e(tableName, "[ERROR]: Unable to insert contact to table. Invalid Contact!\n")
        }
        return false
    }

    fun doesContactWithCustomerIdExist(customerId: String) : Boolean {
        // try to get cursor for matching contact
        val escapedStr = DatabaseUtils.sqlEscapeString(customerId)
        val where = "$CUSTOMER_ID_COL=$escapedStr"
        val cursor = getCursorFromDatabase(where)
        // return if exists or not, close cursor
        var exists = false
        if (cursor != null && cursor.moveToFirst()) {
            exists = true
            cursor.close()
        }
        return exists
    }

    private fun getCursorListAsObjects(c: Cursor) : List<Contact> {
        val list: MutableList<Contact> = emptyList<Contact>().toMutableList()
        if (c.moveToFirst())
        {
            do
            {
                // column by column, parse together a new contact, keep in mind the type of value for each column
                val temp = Contact.getEmptyContact()
                temp.customerId =  DatabaseHelper.getColumnValueAsString(CUSTOMER_ID_COL, c)
                temp.companyName = DatabaseHelper.getColumnValueAsString(COMPANY_NAME_COL, c)
                temp.contactName = DatabaseHelper.getColumnValueAsString(CONTACT_NAME_COL, c)
                temp.contactTitle = DatabaseHelper.getColumnValueAsString(CONTACT_TITLE_COL, c)
                temp.address = DatabaseHelper.getColumnValueAsString(ADDRESS_COL, c)
                temp.city = DatabaseHelper.getColumnValueAsString(CITY_COL, c)
                temp.email = DatabaseHelper.getColumnValueAsString(EMAIL_COL, c)
                temp.postalCode = DatabaseHelper.getColumnValueAsString(POSTAL_CODE_COL, c)
                temp.country = DatabaseHelper.getColumnValueAsString(COUNTRY_COL, c)
                temp.phone = DatabaseHelper.getColumnValueAsString(PHONE_COL, c)
                temp.fax = DatabaseHelper.getColumnValueAsString(FAX_COL, c)
                temp.selected = DatabaseHelper.getColumnValueAsInt(SELECTED_COL, c)
                temp.groupId = DatabaseHelper.getColumnValueAsInt(ROW_ID, c)
                list.add(temp)
            }
            while (c.moveToNext())
        }

        return list.toList()
    }
}
