package com.example.basicaddressbook.contact

import com.example.basicaddressbook.R
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import java.lang.Exception


class ContactCursorAdapter(context: Context?, cursor: Cursor?, flag: Int ) : SimpleCursorAdapter(context, 0, cursor, null, null)
{
    private var mContext : Context? = context
    private var mCursor : Cursor? = cursor

    override fun newView(ctx : Context?, p1: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(ctx).inflate(R.layout.cell_contact, parent, false);
    }

    override fun swapCursor(newCursor: Cursor?): Cursor {
        mContext
        return super.swapCursor(newCursor)
    }

    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        view = LayoutInflater.from(parent.context).inflate(R.layout.cell_contact, parent, false)

        var customerIdTextView = view?.findViewById<TextView>(R.id.cell_textview_customer_id)
        var companyNameTextView = view?.findViewById<TextView>(R.id.cell_textview_company_name)
        var contactNameTextView = view?.findViewById<TextView>(R.id.cell_textview_contact_name)
        var contactTitleTextView = view?.findViewById<TextView>(R.id.cell_textview_contact_title)
        var addressTextView = view?.findViewById<TextView>(R.id.cell_textview_address)
        var cityTextView = view?.findViewById<TextView>(R.id.cell_textview_city)
        var emailTextView = view?.findViewById<TextView>(R.id.cell_textview_email)
        var postalCodeTextView = view?.findViewById<TextView>(R.id.cell_textview_postalcode)
        var countryTextView = view?.findViewById<TextView>(R.id.cell_textview_country)
        var phoneTextView = view?.findViewById<TextView>(R.id.cell_textview_phone)
        var faxTextView = view?.findViewById<TextView>(R.id.cell_textview_fax)

        try {
            customerIdTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.CUSTOMER_ID_COL))
            companyNameTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.COMPANY_NAME_COL))
            contactNameTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.CONTACT_NAME_COL))
            contactTitleTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(
                ContactListTable.CONTACT_TITLE_COL))
            addressTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.ADDRESS_COL))
            cityTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.CITY_COL))
            emailTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.EMAIL_COL))
            postalCodeTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.POSTAL_CODE_COL))
            countryTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.COUNTRY_COL))
            phoneTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.PHONE_COL))
            faxTextView?.text = cursor?.getString(cursor.getColumnIndexOrThrow(ContactListTable.FAX_COL))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return convertView
    }

    override fun bindView(view: View?, ctx: Context?, cursor: Cursor?)
    {

    }


}