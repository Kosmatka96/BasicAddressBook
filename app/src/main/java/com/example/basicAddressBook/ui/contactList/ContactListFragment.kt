package com.example.basicAddressBook.ui.contactList

import android.Manifest.permission.CALL_PHONE
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicAddressBook.R
import com.example.basicAddressBook.adapter.ContactAdapter
import com.example.basicAddressBook.database.ContactListTable
import com.example.basicAddressBook.database.PrefHelper
import com.example.basicAddressBook.databinding.FragmentContactListBinding
import com.example.basicAddressBook.model.Contact
import com.example.basicAddressBook.model.ContactFactory
import com.example.basicAddressBook.ui.createContact.CreateContactFragment

class ContactListFragment : Fragment(), MenuProvider {

    companion object {
        const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4
    }

    private val contactViewModel: ContactListViewModel by viewModels()
    private lateinit var contactTable: ContactListTable
    private lateinit var prefs: PrefHelper

    private lateinit var menuItemImportXML: MenuItem
    private lateinit var menuItemImportJSON: MenuItem
    private lateinit var menuItemSortCustomerId: MenuItem
    private lateinit var menuItemSortCompanyName: MenuItem
    private lateinit var menuItemSortContactName: MenuItem
    private lateinit var menuItemSortAddress: MenuItem
    private lateinit var menuItemSortContactTitle: MenuItem
    private lateinit var menuItemSortCity: MenuItem
    private lateinit var menuItemSortEmail: MenuItem
    private lateinit var menuItemSortPostalCode: MenuItem
    private lateinit var menuItemSortCountry: MenuItem
    private lateinit var menuItemSortPhone: MenuItem
    private lateinit var menuItemSortFax: MenuItem

    private var toolBar: ActionBar? = null
    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private val contactAdapter: ContactAdapter = ContactAdapter(null,
        object : ContactAdapter.ItemClickListener {
            override fun onItemClick(contact: Contact) {
                clickedContactCard(contact)
            }},
        object : ContactAdapter.EmailClickListener {
            override fun onEmailClick(contact: Contact) {
                clickedContactEmail(contact)
            }},
        object : ContactAdapter.AddressClickListener {
            override fun onAddressClick(contact: Contact) {
                clickedContactAddress(contact)
            }},
        object : ContactAdapter.PhoneClickListener {
            override fun onPhoneClick(contact: Contact) {
                clickedContactPhone(contact)
            }})

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Init database and user preferences
        contactTable = ContactListTable.getInstance(requireContext())
        prefs = PrefHelper(requireContext().applicationContext)


        // bind our ViewModel
        val contactViewModelProvider = ViewModelProvider(this)[ContactListViewModel::class.java]
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // bind missingTextView that will be shown when there is nothing in the list
        val missingTextView: TextView = binding.textMissingTextview
        // bind adapter to our RecyclerView
        val contactRecyclerView: RecyclerView = binding.recyclerviewContactList
        contactRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactRecyclerView.adapter = contactAdapter


        contactViewModelProvider.contactListData.observe(viewLifecycleOwner) {
            contactAdapter.setNewList(it)

            if(it != null && it.isNotEmpty()) {
                missingTextView.visibility = View.GONE
                toolBar?.title = "Contacts (${it.size})"
            }
            else {
                missingTextView.visibility = View.VISIBLE
                toolBar?.title = "Contacts (0)"
            }
        }
        refreshContactList()

        // add custom menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            this, viewLifecycleOwner, Lifecycle.State.RESUMED
        )


        return root
    }

    private fun clickedContactCard(contact: Contact) {
        // show options menu to edit or delete contact

        // display dialogue asking user if they want to update or delete this contact
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogue_3_options)
        val title = dialog.findViewById(R.id.custom_dialogue_title) as TextView
        val body = dialog.findViewById(R.id.custom_dialogue_body) as TextView
        title.text = getString(R.string.dialogue_contact_selected, Contact.getDisplayId(contact))
        body.text = getString(R.string.dialogue_contact_ask)
        val updateBtn = dialog.findViewById(R.id.btn_1) as Button
        val deleteBtn = dialog.findViewById(R.id.btn_2) as Button
        val dismissBtn = dialog.findViewById(R.id.btn_3) as Button
        deleteBtn.text = getString(R.string.label_delete)
        updateBtn.setOnClickListener {
            dialog.dismiss()
            // forward the selected contact details to create new contact fragment and navigate to
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
            val navController = navHostFragment.navController
            val bundle = Bundle()
            bundle.putSerializable(CreateContactFragment.keyBundleContact, contact)
            navController.navigate(R.id.nav_create_contact, bundle)
        }
        deleteBtn.setOnClickListener {
            dialog.dismiss()
            // delete this specific instance and refresh list
            contactTable.deleteWithRow(contact.groupId)
            refreshContactList()
        }
        dismissBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

        /*
        // toggle selected attribute, which will show as a darkened background on card
        if(contact.selected == 0) contact.selected = 1
        else contact.selected = 0
        var listOfContacts = contactViewModel.contactListData.value?.toMutableList()
        if (listOfContacts != null) {
            listOfContacts[position] = contact
            contactViewModel.refreshListWithDatabase(listOfContacts.toList())
        }
        */
    }

    private fun clickedContactEmail(contact: Contact) {
        // email contact
        val email = contact.email
        if (email.isNotEmpty() && email.isNotBlank()) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                setDataAndType(Uri.parse("mailto:"), "text/plain")
                putExtra(Intent.EXTRA_EMAIL, email)
                putExtra(Intent.EXTRA_SUBJECT, "Contacting Email")
            }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                intent.setPackage("com.google.android.gm")
                startActivity(intent)
            } else {
                Log.d("ContactList", "No app available to send email.")
            }
        }
    }

    private fun clickedContactPhone(contact: Contact) {
        try {
            // phone contact
            val phone = contact.phone
            if (phone.isNotEmpty() && phone.isNotBlank() && phone.first() != '0') {

                val number: Int = phone.toInt()
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$number")
                if (ActivityCompat.checkSelfPermission(requireContext(), CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            CALL_PHONE)) {
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(),
                            arrayOf(CALL_PHONE),
                            MY_PERMISSIONS_REQUEST_CALL_PHONE)
                    }
                }
                startActivity(callIntent)

                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickedContactAddress(contact: Contact) {
        // get directions for contact
        val address = contact.address
        if (address.isNotEmpty() && address.isNotBlank()) {
            val gmmIntentUri = Uri.parse("google.streetview:cbll=$address")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun removeAllContacts() {
        // drop sql table and clear list
        contactTable.emptyTable()
        contactViewModel.refreshListWithDatabase(null)
    }

    private fun addAllContacts() {
        // import contacts using saved input preference (json/xml) into database
        val importedContacts =
            if (prefs.loadBoolean(PrefHelper.KEY_PREFER_JSON)) ContactFactory.getContactListFromJSON(requireContext())
            else ContactFactory.getContactListFromXML(requireContext())
        contactTable.addListOfContacts(importedContacts)

        refreshContactList()
    }

    private fun refreshContactList() {
        // refresh list with updated contacts in database
        val updatedList: List<Contact>? = contactTable.getAllContacts(prefs.loadString(PrefHelper.KEY_PREFERRED_SORTING_METHOD))
        contactViewModel.refreshListWithDatabase(updatedList)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean = when(item.itemId) {

        // Contact sorting options
        R.id.menu_sort_customer_id -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.CUSTOMER_ID_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_company_name -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.COMPANY_NAME_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_contact_name -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.CONTACT_NAME_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_address -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.ADDRESS_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_contact_title -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.CONTACT_TITLE_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_city -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.CITY_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_email -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.EMAIL_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_postalcode -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.POSTAL_CODE_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_country -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.COUNTRY_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_phone -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.PHONE_COL)
            updateCheckedSortingMenuItems()
            true
        }
        R.id.menu_sort_fax -> {
            prefs.save(PrefHelper.KEY_PREFERRED_SORTING_METHOD, ContactListTable.FAX_COL)
            updateCheckedSortingMenuItems()
            true
        }

        // Import Style options
        R.id.menu_import_style_xml -> {
            prefs.save(PrefHelper.KEY_PREFER_JSON, false)
            updateCheckedImportStyleMenuItems()
            true
        }
        R.id.menu_import_style_json -> {
            prefs.save(PrefHelper.KEY_PREFER_JSON, true)
            updateCheckedImportStyleMenuItems()
            true
        }

        // Import/Export options
        R.id.menu_action_contact_list_import -> {
            addAllContacts()
            true
        }
        R.id.menu_action_contact_list_remove -> {
            removeAllContacts()
            true
        }

        else -> false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_contact_list, menu)
        toolBar = (activity as AppCompatActivity).supportActionBar

        // init import style menu options
        menuItemImportXML = menu.findItem(R.id.menu_import_style_xml)
        menuItemImportJSON = menu.findItem(R.id.menu_import_style_json)
        updateCheckedImportStyleMenuItems()

        // init contact sorting menu options
        menuItemSortCustomerId = menu.findItem(R.id.menu_sort_customer_id)
        menuItemSortCompanyName= menu.findItem(R.id.menu_sort_company_name)
        menuItemSortContactName= menu.findItem(R.id.menu_sort_contact_name)
        menuItemSortAddress= menu.findItem(R.id.menu_sort_address)
        menuItemSortContactTitle= menu.findItem(R.id.menu_sort_contact_title)
        menuItemSortCity= menu.findItem(R.id.menu_sort_city)
        menuItemSortEmail= menu.findItem(R.id.menu_sort_email)
        menuItemSortPostalCode= menu.findItem(R.id.menu_sort_postalcode)
        menuItemSortCountry= menu.findItem(R.id.menu_sort_country)
        menuItemSortPhone= menu.findItem(R.id.menu_sort_phone)
        menuItemSortFax= menu.findItem(R.id.menu_sort_fax)
        updateCheckedSortingMenuItems()

    }

    private fun isSortingSetAs(sortBy: String) : Boolean {
        return (prefs.loadString(PrefHelper.KEY_PREFERRED_SORTING_METHOD).equals(sortBy))
    }

    private fun updateCheckedSortingMenuItems() {
        // uncheck all sorting options
        menuItemSortCustomerId.isChecked = false
        menuItemSortCompanyName.isChecked = false
        menuItemSortContactName.isChecked = false
        menuItemSortAddress.isChecked = false
        menuItemSortContactTitle.isChecked = false
        menuItemSortCity.isChecked = false
        menuItemSortEmail.isChecked = false
        menuItemSortEmail.isChecked = false
        menuItemSortPostalCode.isChecked = false
        menuItemSortCountry.isChecked = false
        menuItemSortPhone.isChecked = false
        menuItemSortFax.isChecked = false

        // using shared preferences, update the checked sorting option to match saved preference
        if (isSortingSetAs(ContactListTable.CUSTOMER_ID_COL)) menuItemSortCustomerId.isChecked = true
        else if (isSortingSetAs(ContactListTable.COMPANY_NAME_COL)) menuItemSortCompanyName.isChecked = true
        else if (isSortingSetAs(ContactListTable.CONTACT_NAME_COL)) menuItemSortContactName.isChecked = true
        else if (isSortingSetAs(ContactListTable.ADDRESS_COL)) menuItemSortAddress.isChecked = true
        else if (isSortingSetAs(ContactListTable.CONTACT_TITLE_COL)) menuItemSortContactTitle.isChecked = true
        else if (isSortingSetAs(ContactListTable.CITY_COL)) menuItemSortCity.isChecked = true
        else if (isSortingSetAs(ContactListTable.EMAIL_COL)) menuItemSortEmail.isChecked = true
        else if (isSortingSetAs(ContactListTable.POSTAL_CODE_COL)) menuItemSortPostalCode.isChecked = true
        else if (isSortingSetAs(ContactListTable.COUNTRY_COL)) menuItemSortCountry.isChecked = true
        else if (isSortingSetAs(ContactListTable.PHONE_COL)) menuItemSortPhone.isChecked = true
        else if (isSortingSetAs(ContactListTable.FAX_COL)) menuItemSortFax.isChecked = true

        refreshContactList()
    }

    private fun updateCheckedImportStyleMenuItems() {
        if (prefs.loadBoolean(PrefHelper.KEY_PREFER_JSON)) {
            menuItemImportXML.isChecked = false
            menuItemImportJSON.isChecked = true
        }
        else {
            menuItemImportXML.isChecked = true
            menuItemImportJSON.isChecked = false
        }
    }

    override fun onPrepareMenu(menu: Menu) {
    }

    override fun onResume() {
        super.onResume()

        // ping observer again, as fragment on the initial drawer gets its app bar title rewritten at first
        contactViewModel.refreshListWithDatabase(contactViewModel.contactListData.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}