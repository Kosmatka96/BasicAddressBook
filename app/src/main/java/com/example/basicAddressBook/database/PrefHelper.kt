package com.example.basicAddressBook.database

import android.content.Context
import android.content.SharedPreferences

// A small helper class that helps with loading/saving data within the user shared preferences.
// Ideal for saving short information like user settings, etc, all other data should be in an
// sql table, see: DatabaseHelper and AbstractTable...

class PrefHelper(val context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFERENCES_NAME = "PreferenceHelper"
        const val KEY_DATABASE_VERSION = "key_database_version"
        const val KEY_PREFER_JSON = "key_prefer_json"
        const val KEY_PREFERRED_SORTING_METHOD = "key_preferred_sorting_method"
    }

    fun save(KEY: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY, value)
        editor.apply()
    }

    fun save(KEY: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY, value)
        editor.apply()
    }

    fun save(KEY: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY, status)
        editor.apply()
    }

    fun loadString(KEY: String): String? {
        return sharedPref.getString(KEY, null)
    }

    fun loadInt(KEY: String): Int {
        return sharedPref.getInt(KEY, 0)
    }

    fun loadBoolean(KEY: String): Boolean {
        return sharedPref.getBoolean(KEY, false)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

}
