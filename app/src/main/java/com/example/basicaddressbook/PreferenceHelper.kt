package com.example.basicaddressbook

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(val context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "PreferenceHelper"

        enum class CONTACT_SORT_SELECTION {
            CONTACT_SORT_CUSTOMER_ID,
            CONTACT_SORT_COMPANY_NAME,
            CONTACT_SORT_CONTACT_NAME,
            CONTACT_SORT_CONTACT_TITLE,
            CONTACT_SORT_ADDRESS ,
            CONTACT_SORT_CITY,
            CONTACT_SORT_EMAIL,
            CONTACT_SORT_POSTALCODE,
            CONTACT_SORT_COUNTRY,
            CONTACT_SORT_PHONE,
            CONTACT_SORT_FAX
        }

        const val KEY_PREFER_JSON = "key_prefer_json"
        const val KEY_PREFERRED_SORTING_METHOD = "key_preferred_sorting_method"

    }


    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun save(KEY: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY, value)
        editor.commit()
    }

    fun save(KEY: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY, value)
        editor.commit()
    }

    fun save(KEY: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY, status)
        editor.commit()
    }

    fun getValueString(KEY: String): String? {
        return sharedPref.getString(KEY, null)
    }

    fun getValueInt(KEY: String): Int {
        return sharedPref.getInt(KEY, 0)
    }

    fun getValueBoolean(KEY: String): Boolean? {
        return sharedPref.getBoolean(KEY, false)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.commit()
    }

    fun removeValue(KEY: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY)
        editor.commit()
    }

}
