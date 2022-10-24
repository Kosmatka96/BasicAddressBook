package com.example.basicaddressbook.ui.createContact

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.basicaddressbook.R
import com.example.basicaddressbook.databinding.FragmentCreateContactBinding
import com.example.basicaddressbook.ui.contactList.ContactListViewModel

class CreateContactFragment : Fragment(), MenuProvider {

    private var _binding: FragmentCreateContactBinding? = null
    private val createContactViewModel: ContactListViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val createContactViewModelProvider =
            ViewModelProvider(this)[CreateContactViewModel::class.java]

        _binding = FragmentCreateContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val customerIdTextView: TextView = binding.crTextviewCustomerId
        val companyNameTextView: TextView = binding.crTextviewCompanyName
        val contactNameTextView: TextView = binding.crTextviewContactName
        val contactTitleTextView: TextView = binding.crTextviewContactTitle
        val addressTextView: TextView = binding.crTextviewAddress
        val cityTextView: TextView = binding.crTextviewCity
        val emailTextView: TextView = binding.crTextviewEmail
        val postalCodeTextView: TextView = binding.crTextviewPostalcode
        val countryTextView: TextView = binding.crTextviewCountry
        val phoneTextView: TextView = binding.crTextviewPhone
        val faxTextView: TextView = binding.crTextviewFax
        createContactViewModelProvider.newContact.observe(viewLifecycleOwner) {
            customerIdTextView.text = it.customerId
            companyNameTextView.text = it.companyName
            contactNameTextView.text = it.contactName
            contactTitleTextView.text = it.contactTitle
            addressTextView.text = it.address
            cityTextView.text = it.city
            emailTextView.text = it.email
            postalCodeTextView.text = it.postalCode
            countryTextView.text = it.country
            phoneTextView.text = it.phone
            faxTextView.text = it.fax
        }

        // add custom menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            this, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
        return root
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        // Still was going to finish this up with selecting and tailoring the ListView based on user preferences....
        R.id.menu_action_add_contact -> true
        R.id.menu_action_add_reset -> true
        R.id.menu_action_dismiss -> true
        else -> false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_create_call, menu)
    }

    override fun onPrepareMenu(menu: Menu) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}