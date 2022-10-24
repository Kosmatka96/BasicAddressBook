package com.example.basicaddressbook.ui.contactList

import android.Manifest.permission.CALL_PHONE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicaddressbook.R
import com.example.basicaddressbook.adapter.ContactAdapter
import com.example.basicaddressbook.database.ContactListTable
import com.example.basicaddressbook.database.PrefHelper
import com.example.basicaddressbook.databinding.FragmentContactListBinding
import com.example.basicaddressbook.model.Contact
import com.example.basicaddressbook.model.ContactFactory

class ContactListFragment : Fragment(), MenuProvider {

    companion object {
        const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4
    }

    private val contactViewModel: ContactListViewModel by viewModels()
    private lateinit var contactTable: ContactListTable
    private lateinit var prefs: PrefHelper
    private var toolBar: ActionBar? = null

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private val contactAdapter: ContactAdapter = ContactAdapter(null,
        object : ContactAdapter.ItemClickListener {
            override fun onItemClick(contact: Contact, position: Int) {
                clickedContactCard(contact, position)
            }},
        object : ContactAdapter.EmailClickListener {
            override fun onEmailClick(contact: Contact, position: Int) {
                clickedContactEmail(contact, position)
            }},
        object : ContactAdapter.AddressClickListener {
            override fun onAddressClick(contact: Contact, position: Int) {
                clickedContactAddress(contact, position)
            }},
        object : ContactAdapter.PhoneClickListener {
            override fun onPhoneClick(contact: Contact, position: Int) {
                clickedContactPhone(contact, position)
            }})

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Init database and user preferences
        contactTable = ContactListTable(requireContext().applicationContext)
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
        contactViewModel.refreshListWithDatabase(contactTable.getAllContacts())

        // add custom menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            this, viewLifecycleOwner, Lifecycle.State.RESUMED
        )


        return root
    }

    private fun clickedContactCard(contact: Contact, position: Int) {
        // toggle selected attribute, which will show as a darkened background on card
        if(contact.selected == 0) contact.selected = 1
        else contact.selected = 0
        var listOfContacts = contactViewModel.contactListData.value?.toMutableList()
        if (listOfContacts != null) {
            listOfContacts[position] = contact
            contactViewModel.refreshListWithDatabase(listOfContacts.toList())
        }
    }

    private fun clickedContactEmail(contact: Contact, position: Int) {
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

    private fun clickedContactPhone(contact: Contact, position: Int) {
        try {
            // phone contact
            val phone = contact.phone
            if (phone.isNotEmpty() && phone.isNotBlank() && !phone.first().equals("0")) {

                val number: Int = phone.toString().toInt()
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

                val intent = Intent(Intent.ACTION_CALL);
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickedContactAddress(contact: Contact, position: Int) {
        // get directions for contact
        val address = contact.address
        if (address.isNotEmpty() && address.isNotBlank()) {
            val gmmIntentUri = Uri.parse("google.streetview:cbll=$address")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent)
        }
    }

    private fun removeAllContacts() {
        // drop sql table and clear list
        contactTable.emptyTable()
        contactViewModel.refreshListWithDatabase(null)
    }

    private fun addAllContacts() {
        // convert locally saved resource file into a list of Contacts, then save those in the database
        // choose which resource file based off of saved user preferences...
        if (prefs.loadBoolean(PrefHelper.KEY_PREFER_JSON) == true)
            contactTable.addListOfContacts(ContactFactory.getContactListFromJSON(requireContext()))
        else
            contactTable.addListOfContacts(ContactFactory.getContactListFromXML(requireContext()))

        val updatedList: List<Contact>? = contactTable.getAllContacts()
        contactViewModel.refreshListWithDatabase(updatedList)
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