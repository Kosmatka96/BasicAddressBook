package com.example.basicAddressBook.model

import android.content.Context
import android.util.Log
import com.example.basicAddressBook.database.ContactListTable
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.nio.charset.Charset
import kotlin.Exception

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
            val json: String?
            try {
                val inputStream = context.assets.open(JSON_FILE_NAME)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                val charset: Charset = Charsets.UTF_8
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, charset)
            }
            catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }

            var contactList: MutableList<Contact> = emptyList<Contact>().toMutableList()
            try {
                val obj = JSONObject(json)
                val contactArray = obj.getJSONObject("AddressBook").getJSONArray("Contact")
                for (i in 0 until contactArray.length()) {
                    val contactDetail = contactArray.getJSONObject(i)
                    val newContact = Contact.getEmptyContact()

                    try {
                        newContact.customerId = contactDetail.getString(ContactListTable.CUSTOMER_ID_COL)
                        newContact.companyName = contactDetail.getString(ContactListTable.COMPANY_NAME_COL)
                        newContact.contactName = contactDetail.getString(ContactListTable.CONTACT_NAME_COL)
                        newContact.contactTitle = contactDetail.getString(ContactListTable.CONTACT_TITLE_COL)
                        newContact.address = contactDetail.getString(ContactListTable.ADDRESS_COL)
                        newContact.city = contactDetail.getString(ContactListTable.CITY_COL)
                        newContact.email = contactDetail.getString(ContactListTable.EMAIL_COL)
                        newContact.postalCode = contactDetail.getString(ContactListTable.POSTAL_CODE_COL)
                        newContact.country = contactDetail.getString(ContactListTable.COUNTRY_COL)
                        newContact.phone = contactDetail.getString(ContactListTable.PHONE_COL)
                        newContact.fax = contactDetail.getString(ContactListTable.FAX_COL)
                    } catch (e: Exception) {

                    }

                    contactList.add(newContact)
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }
            return contactList
        }
    }


}
