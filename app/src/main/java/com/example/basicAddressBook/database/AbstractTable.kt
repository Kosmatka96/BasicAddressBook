package com.example.basicAddressBook.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

// Base DataTable Utility class that all new tables should inherit from
// provides basic utilities like inserting/updating/fetching from the
// corresponding table.
// Inherited classes must define their own columns and table name and keys

abstract class AbstractTable(context: Context) {

    open var tableName = "abstract_table"
    open var keys = arrayOf(ROW_ID)

    private var db: SQLiteDatabase? = null
    private var dbHelper: DatabaseHelper? = null

    init {
        dbHelper = DatabaseHelper.getInstance(context)
    }

    companion object {
        const val ROW_ID = "_id"
    }

    fun close() {
        if (db != null) {
            db?.close()
            db = null
        }
    }

    fun emptyTable() {
        if (db == null) db = dbHelper?.readableDatabase
        db?.delete(tableName, null, null)
    }

    fun getCursorFromDatabase(where: String?, orderBy: String? = null) : Cursor? {
        if (db == null) db = dbHelper?.readableDatabase
        return db?.query(tableName, keys, where, null, null, null, orderBy)
    }

    fun insertOrUpdate(select: String, newContentValues: ContentValues) {
        try {
            db = dbHelper?.writableDatabase
            db?.execSQL("PRAGMA foreign_keys = OFF;")
            val oldCursor = getCursorFromDatabase(select)

            if ((oldCursor != null) && oldCursor.moveToFirst()) {
                // record exists, update
                var row = -1
                val columnIndex = oldCursor.getColumnIndex(ROW_ID)
                if (columnIndex > -1) row = oldCursor.getInt(columnIndex)
                if (row > -1)  db?.update(tableName, newContentValues, "$ROW_ID=$row", null)
                oldCursor.close()
            }
            else {
                // no record, create one
                db?.insert(tableName, null, newContentValues)
            }
        } catch (e: Exception) {
            Log.e(tableName, "Error during insertOrUpdate method in AbstractTable! ${e.printStackTrace()}")
        }
    }

    fun forceInsert(newContentValues: ContentValues) {
        try {
            db = dbHelper?.writableDatabase
            db?.execSQL("PRAGMA foreign_keys = OFF;")
            db?.insert(tableName, null, newContentValues)
        } catch (e: Exception) {
            Log.e(tableName, "Error during forceInsert method in AbstractTable! ${e.printStackTrace()}")
        }
    }

    fun deleteWithRow(row: Int) {
        try {
            db = dbHelper?.writableDatabase
            db?.execSQL("PRAGMA foreign_keys = OFF;")
            db?.delete(tableName, "$ROW_ID=$row", null)
        } catch (e: Exception) {
            Log.e(tableName, "Error during deleteWithRow method in AbstractTable! ${e.printStackTrace()}")
        }

    }
}