package com.example.basicaddressbook.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import com.example.basicaddressbook.model.Contact


// Database object that handles the table of contacts, defines data table and provides an interface for changing/retrieving data

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

        const val createTableQuery = ("CREATE TABLE " + TABLE_NAME + " (" +
                ROW_ID + " TEXT PRIMARY KEY, " +
                CUSTOMER_ID_COL + " TEXT," +
                COMPANY_NAME_COL + " TEXT," +
                CONTACT_NAME_COL + " TEXT," +
                CONTACT_TITLE_COL + " TEXT," +
                ADDRESS_COL + " TEXT," +
                CITY_COL + " TEXT," +
                EMAIL_COL + " TEXT," +
                POSTAL_CODE_COL + " TEXT," +
                COUNTRY_COL + " TEXT," +
                PHONE_COL + " TEXT," +
                FAX_COL + " TEXT," +
                SELECTED_COL + " INTEGER" + ")")

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
    }

    override var keys = arrayOf(ROW_ID, CUSTOMER_ID_COL, COMPANY_NAME_COL, CONTACT_NAME_COL, CONTACT_TITLE_COL, ADDRESS_COL, CITY_COL,
        EMAIL_COL, POSTAL_CODE_COL, COUNTRY_COL, PHONE_COL, FAX_COL, SELECTED_COL)

    fun getAllContacts(): List<Contact>? {
        // get all data from our database
        openReadable()
        val cursor =  db?.query(TABLE_NAME, keys, null, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val listAsObjects = getCursorListAsObjects(cursor)
            cursor.close()
            return listAsObjects
        }
        return null
    }

    fun doesContactWithCustomerIdExist(customerId: String) : Boolean {
        // try to get cursor for matching contact
        openReadable()
        val escapedStr = DatabaseUtils.sqlEscapeString(customerId)
        val where = "WHERE $CUSTOMER_ID_COL = $escapedStr"
        val cursor = db?.rawQuery("SELECT * FROM  $TABLE_NAME", null)

        // return if exists or not, close cursor
        var exists = false
        if (cursor != null && cursor.moveToFirst()) {
            exists = true
            cursor.close()
        }

        return exists
    }

    fun addListOfContacts(newListOfContacts: List<Contact>?) {
        if (newListOfContacts != null && newListOfContacts.isNotEmpty()) {
            openWriteable()
            if (db != null) {
                newListOfContacts.forEach {
                    // add into database
                    val select = "$ROW_ID=${DatabaseUtils.sqlEscapeString(it.customerId)}"
                    insertOrUpdate(TABLE_NAME, select, getContentValues(it))
                }
            }
        }
    }

    private fun getContentValues(contact: Contact) : ContentValues {
        // add each property of Contact in the form of a key value pair to insert into table
        val values = ContentValues()
        values.put(ROW_ID, contact.customerId)
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

    private fun getCursorListAsObjects(c: Cursor) : List<Contact> {
        val list: MutableList<Contact> = emptyList<Contact>().toMutableList()
        if (c.moveToFirst())
        {
            do
            {
                // Converting from a cursor to a Contact object for ease of use, ideally a DAO+RoomDatabase would have been cleaner
                // there were multiple ways I could have approached this...
                val temp = Contact.getEmptyContact()
                temp.customerId = c.getString(1)
                temp.companyName = c.getString(2)
                temp.contactName = c.getString(3)
                temp.contactTitle = c.getString(4)
                temp.address = c.getString(5)
                temp.city = c.getString(6)
                temp.email = c.getString(7)
                temp.postalCode = c.getString(8)
                temp.country = c.getString(9)
                temp.phone = c.getString(10)
                temp.fax = c.getString(11)
                temp.selected = c.getInt(12)
                list.add(temp)
            }
            while (c.moveToNext())
        }

        return list.toList()
    }

    fun emptyTable() {
        openReadable()
        db?.delete(TABLE_NAME, null, null)
    }
}
