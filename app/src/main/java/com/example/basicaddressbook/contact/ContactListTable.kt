package com.example.basicaddressbook.contact

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import java.lang.Exception

class ContactListTable(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    public val CONTENT_URI: Uri = Uri.parse("content://com.example.basicaddressbook/contacts")

    companion object{
        val CONTENT_URI: Uri = Uri.parse("content://com.example.basicaddressbook/contacts")
        private const val DATABASE_NAME = "DATABASE_BASIC_ADDRESS_BOOK"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "contact_list_table"
        const val ROW_ID = "_id"
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

        val keys = arrayOf<String>(ROW_ID, CUSTOMER_ID_COL, COMPANY_NAME_COL, CONTACT_NAME_COL, CONTACT_TITLE_COL, ADDRESS_COL, CITY_COL, EMAIL_COL, POSTAL_CODE_COL, COUNTRY_COL, PHONE_COL, FAX_COL)


        private fun insertOrUpdate(table: String, select: String, updateVals: ContentValues, db: SQLiteDatabase) {
            try {
                val oldCursor = db.query(table, null, select, null, null, null, null)

                if ( oldCursor != null && oldCursor.moveToFirst()) {
                    // record exists, update
                    var row = -1
                    try {
                        val columnIndex = oldCursor.getColumnIndex(ROW_ID)
                        if (columnIndex > -1) row = oldCursor.getInt(columnIndex)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (row > -1)  db.update(table, updateVals, "$ROW_ID = $row", null)
                }
                else {
                    // no record, create one
                    db.insert(table, null, updateVals)
                }
                oldCursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " (" +
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
                FAX_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun emptyTable() {
        try {
            val db = this.readableDatabase
            db.delete(TABLE_NAME, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllContactsCursor(): Cursor? {
        try {
            // get all data from our database
            val db = this.readableDatabase
            return db.query(TABLE_NAME, keys, null, null, null, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Here is where I would implement the cursor fetches for when the user taps a certain filter in the contact list screen
    // just ran out of time a bit....




    // Was going to use this function to help determine duplicates...
    fun doesContactWithCustomerIdExist(customerId: String) : Boolean {
        // try to get cursor for matching contact
        val db = this.readableDatabase
        val escapedStr = DatabaseUtils.sqlEscapeString(customerId)
        val where = "WHERE $CUSTOMER_ID_COL = $escapedStr"
        val cursor = db.rawQuery("SELECT * FROM  $TABLE_NAME", null)

        // return if exists or not, close cursor
        var exists = false
        if (cursor.moveToFirst()) exists = true
        cursor.close()
        return exists
    }

    fun addListOfContacts(newListOfContacts: List<Contact>?){
        val db = this.writableDatabase

        if (newListOfContacts != null && newListOfContacts.isNotEmpty()) {
            newListOfContacts.forEach {
                // add each property of Contact in the form of a key value pair to insert into table
                val values = ContentValues()
                values.put(ROW_ID, it.customerId)
                values.put(CUSTOMER_ID_COL, it.customerId)
                values.put(COMPANY_NAME_COL, it.companyName)
                values.put(CONTACT_NAME_COL, it.contactName)
                values.put(CONTACT_TITLE_COL, it.contactTitle)
                values.put(ADDRESS_COL, it.address)
                values.put(CITY_COL, it.city)
                values.put(EMAIL_COL, it.email)
                values.put(POSTAL_CODE_COL, it.postalCode)
                values.put(COUNTRY_COL, it.country)
                values.put(PHONE_COL, it.phone)
                values.put(FAX_COL, it.fax)

                // add into database
                insertOrUpdate(TABLE_NAME, "", values, db)
            }
        }

        db.close()
    }


}
