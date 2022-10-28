package com.example.basicAddressBook.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


// This is a helper class for managing data tables. It removes/creates all relevant data tables to be used.
// Future data tables would need to be added to the onCreate and doUpgrade methods respectively

class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "DATABASE_BASIC_ADDRESS_BOOK"
        const val DATABASE_VERSION = 2

        // Singleton pattern, implemented in a multi-threaded safe way
        @Volatile private var INSTANCE:DatabaseHelper? = null
        fun getInstance(context: Context):DatabaseHelper {
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = DatabaseHelper(context, null)
                    INSTANCE = instance
                }
                return instance
            }
        }

        fun getColumnValueAsString(columnName: String, cursor: Cursor) : String {
            var value = ""
            val columnIndex = cursor.getColumnIndex(columnName)
            value = cursor.getString(columnIndex)
            return value
        }

        fun getColumnValueAsInt(columnName: String, cursor: Cursor) : Int {
            var value = 0
            try {
                val columnIndex = cursor.getColumnIndex(columnName)
                value = cursor.getInt(columnIndex)
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "[ERROR]: Unable to get int value from column: $columnName")
            }
            return value
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // cycle through all data tables create query strings and execute them to build database tables
        db.execSQL(ContactListTable.createTableQueryStr)
    }

    fun initAllTables(context: Context) {
        // cycle through all data tables and init them through delcaration
        ContactListTable.getInstance(context)
    }

    fun closeDatabase(context: Context) {
        // cycle through all data tables and close them
        ContactListTable.getInstance(context).close()
        close()
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) { }

    fun doUpgrade() {
        // cycle through all data tables and remove them
        val database: SQLiteDatabase = writableDatabase
        database.execSQL("DROP TABLE IF EXISTS ${ContactListTable.TABLE_NAME}")
        onCreate(database)
    }

}