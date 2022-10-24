package com.example.basicaddressbook.model

import android.content.Context
import android.util.Log
import com.example.basicaddressbook.database.ContactListTable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.lang.Exception

// Builder class that returns a list of parsed Contacts from either an XML or JSON document

class ContactFactory {

    companion object {
        private const val TAG = "ContactFactory"
        private const val JSON_FILE_NAME = "ab.json"
        private const val XML_FILE_NAME = "ab.xml"
        private const val XML_TAG_CONTACT = "Contact"

        fun getContactListFromXML(context: Context): List<Contact>? {
            try {
                // use the built-in XmlPullParser to build list of Contacts from xml resource file
                val contactList = ArrayList<Contact>()
                var contact: Contact = Contact.getEmptyContact()
                val inputStream = context.assets.open(XML_FILE_NAME)
                val parserFactory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
                val parser: XmlPullParser = parserFactory.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
                parser.setInput(inputStream, null)
                var tag: String?
                var text = ""
                var event = parser.eventType
                while (event != XmlPullParser.END_DOCUMENT) {
                    tag = parser.name
                    when (event) {
                        XmlPullParser.START_TAG -> if (tag == XML_TAG_CONTACT) contact = Contact.getEmptyContact()
                        XmlPullParser.TEXT -> text = parser.text

                        XmlPullParser.END_TAG -> when (tag) {
                        ContactListTable.CUSTOMER_ID_COL -> contact.customerId = text
                        ContactListTable.COMPANY_NAME_COL -> contact.companyName = text
                        ContactListTable.CONTACT_NAME_COL -> contact.contactName = text
                        ContactListTable.CONTACT_TITLE_COL -> contact.contactTitle = text
                        ContactListTable.ADDRESS_COL -> contact.address = text
                        ContactListTable.CITY_COL -> contact.city = text
                        ContactListTable.EMAIL_COL -> contact.email = text
                        ContactListTable.POSTAL_CODE_COL -> contact.postalCode = text
                        ContactListTable.COUNTRY_COL -> contact.country = text
                        ContactListTable.PHONE_COL -> contact.phone = text
                        ContactListTable.FAX_COL -> contact.fax = text
                        XML_TAG_CONTACT -> contactList.add(contact)
                    }
                    }
                    event = parser.next()
                }

                return contactList.toList()

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch(e: Exception) {
                Log.e(TAG, "Error occurred during XML parse: ${e.toString()}" )
            }

            return null
        }

        fun getContactListFromJSON(context: Context): List<Contact>? {
            return try {
                // use gson to parse list of Contacts from resource file
                val jsonString = context.assets.open(JSON_FILE_NAME).bufferedReader().use { it.readText() }
                val gson = Gson()
                val listContactType = object : TypeToken<List<Contact>>() {}.type
                var contactList: List<Contact> = gson.fromJson(jsonString, listContactType)
                contactList

            } catch (e: Exception) {
                Log.e(TAG, "Unknown error occurred during JSON parse: ${e.toString()}" )
                null
            }
        }
    }


}
