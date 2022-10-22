package com.example.basicaddressbook.ui.contactList

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.ListView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.basicaddressbook.PreferenceHelper
import com.example.basicaddressbook.R
import com.example.basicaddressbook.contact.ContactCursorAdapter
import com.example.basicaddressbook.contact.ContactFactory
import com.example.basicaddressbook.contact.ContactListTable
import com.example.basicaddressbook.databinding.FragmentContactListBinding
import java.security.Provider

class ContactListFragment : Fragment(), MenuProvider, LoaderManager.LoaderCallbacks<Cursor> {
    private var contactListView : ListView? = null
    private var contactListMissingTextView : TextView? = null
    private val uri: Uri? = null
    private lateinit var mPreferences : PreferenceHelper
    private lateinit var mDataTable : ContactListTable
    private lateinit var contactCursorAdapter : SimpleCursorAdapter

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mDataTable = ContactListTable(requireActivity(), null)

        // add custom menu
        val menuHost: MenuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(
            this, viewLifecycleOwner, Lifecycle.State.RESUMED
        )

        val homeViewModel =
            ViewModelProvider(this)[ContactListViewModel::class.java]

        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMissingTextview
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // get handle for ListView, init with empty adapter
        contactListView = activity?.findViewById(R.id.listview_contact_list)
        contactListMissingTextView = activity?.findViewById(R.id.text_missing_textview)


        val uiBindFrom = arrayOf("_id", "CustomerID", "CompanyName", "ContactName", "ContactTitle", "Address", "City", "Email", "PostalCode", "Country", "Phone", "Fax")
        val uiBindTo = intArrayOf(0, R.id.cell_textview_customer_id, R.id.cell_textview_company_name, R.id.cell_textview_contact_name, R.id.cell_textview_contact_title, R.id.cell_textview_address,
            R.id.cell_textview_city, R.id.cell_textview_email, R.id.cell_textview_postalcode, R.id.cell_textview_address, R.id.cell_textview_country, R.id.cell_textview_phone, R.id.cell_textview_fax)
        LoaderManager.getInstance(this)
        contactCursorAdapter = SimpleCursorAdapter(requireContext(), R.layout.cell_contact, null, uiBindFrom, uiBindTo,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        contactListView?.adapter = contactCursorAdapter

        // check for already existing contacts, populate List with them )
        mPreferences =  PreferenceHelper(requireContext())
        refreshListWithDatabase()

        return root
    }

    private fun refreshListWithDatabase() {
        LoaderManager.getInstance(this).restartLoader(1, null, this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_contact_list, menu)
    }

    override fun onPrepareMenu(menu: Menu) {

    }

    override fun onMenuItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        // Still was going to finish this up with selecting and tailoring the ListView based on user preferences....
        R.id.menu_action_contact_list_sort -> true
        R.id.menu_sort_customer_id -> true
        R.id.menu_sort_company_name -> true
        R.id.menu_sort_contact_name -> true
        R.id.menu_sort_address -> true
        R.id.menu_sort_contact_title -> true
        R.id.menu_sort_city -> true
        R.id.menu_sort_email -> true
        R.id.menu_sort_postalcode -> true
        R.id.menu_sort_country -> true
        R.id.menu_sort_phone -> true
        R.id.menu_sort_fax -> true

        R.id.menu_action_contact_list_allow_duplicates -> true


        R.id.menu_action_contact_list_import -> {
            // convert locally saved resource file into a list of Contacts, then save those in the database
            // choose which resource file based off of saved user preferences...
            if (mPreferences.getValueBoolean(PreferenceHelper.KEY_PREFER_JSON) == true)
                mDataTable.addListOfContacts(ContactFactory.getContactListFromJSON(requireContext()))
            else
                mDataTable.addListOfContacts(ContactFactory.getContactListFromXML(requireContext()))

            refreshListWithDatabase()

            true
        }

        R.id.menu_action_contact_list_remove -> {
            // only remove table if it actually exists
            val existingListCursor = mDataTable.getAllContactsCursor()
            if (existingListCursor != null && existingListCursor.moveToFirst()) {
                // drop sql table and reload table object
                mDataTable.emptyTable()
                mDataTable = ContactListTable(requireContext(), null)

                refreshListWithDatabase()
            }
            true
        }

        else -> false
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val select : String = "${ContactListTable.ROW_ID} = ${ContactListTable.CUSTOMER_ID_COL}"
        return CursorLoader(requireContext(), ContactListTable.CONTENT_URI, ContactListTable.keys, select, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        contactCursorAdapter.swapCursor(data)
        contactCursorAdapter.notifyDataSetInvalidated()

        // hide/show missing textview depending on if any elements are shown from contacts
        if ((data != null) && data.moveToFirst())
            contactListMissingTextView?.visibility = View.GONE
        else
            contactListMissingTextView?.visibility = View.VISIBLE

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        contactCursorAdapter.swapCursor(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}