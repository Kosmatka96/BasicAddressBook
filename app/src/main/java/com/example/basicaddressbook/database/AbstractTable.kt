package com.example.basicaddressbook.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

abstract class AbstractTable(context: Context) {
    var db: SQLiteDatabase? = null
    private var dbHelper: DatabaseHelper? = null
    open var keys = arrayOf(ROW_ID)

    init {
        dbHelper = DatabaseHelper.getInstance(context)
    }

    companion object {
        const val ROW_ID = "_id"
    }

    fun openReadable() {
        if (db == null) {
            db = dbHelper?.readableDatabase
        }
    }

    fun openWriteable() {
        db = dbHelper?.writableDatabase
        db?.execSQL("PRAGMA foreign_keys = OFF;")
    }

    fun close() {
        if (db == null) {
            dbHelper?.close()
            dbHelper = null
        }
        else {
            db?.close()
            db = null
        }
    }

    fun insertOrUpdate(table: String, select: String, newContentValues: ContentValues) {

        try {
            val oldCursor = db?.query(table, null, select, null, null, null, null)

            if ( oldCursor != null && oldCursor.moveToFirst()) {
                // record exists, update
                var row = -1
                val columnIndex = oldCursor.getColumnIndex(ROW_ID)
                if (columnIndex > -1) row = oldCursor.getInt(columnIndex)
                if (row > -1)  db?.update(table, newContentValues, "$ROW_ID=$row", null)
                oldCursor.close()
            }
            else {
                // no record, create one
                db?.insert(table, null, newContentValues)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}