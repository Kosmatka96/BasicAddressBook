package com.example.basicAddressBook.ui.createContact

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.basicAddressBook.R
import com.example.basicAddressBook.database.ContactListTable
import com.example.basicAddressBook.database.PrefHelper
import com.example.basicAddressBook.databinding.FragmentCreateContactBinding
import com.example.basicAddressBook.model.Contact


class CreateContactFragment : Fragment(), MenuProvider {

    companion object {
        const val keyBundleContact = "bundle_extra_contact"
    }

    private var _binding: FragmentCreateContactBinding? = null
    private val createContactViewModel: CreateContactViewModel by viewModels()
    private lateinit var contactTable: ContactListTable
    private lateinit var prefs: PrefHelper

    // EditTexts
    private lateinit var customerIdView: EditText
    private lateinit var companyNameView: EditText
    private lateinit var contactNameView: EditText
    private lateinit var contactTitleView: EditText
    private lateinit var addressView: EditText
    private lateinit var cityView: EditText
    private lateinit var emailView: EditText
    private lateinit var postalCodeView: EditText
    private lateinit var countryView: EditText
    private lateinit var phoneView: EditText
    private lateinit var faxView: EditText
    // ImageViews
    private lateinit var customerIdArrow: ImageView
    private lateinit var companyNameArrow: ImageView
    private lateinit var contactNameArrow: ImageView
    private lateinit var contactTitleArrow: ImageView
    private lateinit var addressArrow: ImageView
    private lateinit var cityArrow: ImageView
    private lateinit var emailArrow: ImageView
    private lateinit var postalCodeArrow: ImageView
    private lateinit var countryArrow: ImageView
    private lateinit var phoneArrow: ImageView
    private lateinit var faxArrow: ImageView

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Init database and user preferences
        contactTable = ContactListTable.getInstance(requireContext())
        prefs = PrefHelper(requireContext().applicationContext)

        val createContactViewModelProvider =
            ViewModelProvider(this)[CreateContactViewModel::class.java]

        _binding = FragmentCreateContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get handles for UI elements
        // EditTexts
        customerIdView = binding.crTextviewCustomerId
        companyNameView = binding.crTextviewCompanyName
        contactNameView = binding.crTextviewContactName
        contactTitleView = binding.crTextviewContactTitle
        addressView = binding.crTextviewAddress
        cityView = binding.crTextviewCity
        emailView = binding.crTextviewEmail
        postalCodeView = binding.crTextviewPostalcode
        countryView = binding.crTextviewCountry
        phoneView = binding.crTextviewPhone
        faxView = binding.crTextviewFax
        // ImageViews
        customerIdArrow = binding.crImageviewCustomerId
        companyNameArrow = binding.crImageviewCompanyName
        contactNameArrow = binding.crImageviewContactName
        contactTitleArrow = binding.crImageviewContactTitle
        addressArrow = binding.crImageviewAddress
        cityArrow = binding.crImageviewCity
        emailArrow = binding.crImageviewEmail
        postalCodeArrow = binding.crImageviewPostalcode
        countryArrow = binding.crImageviewCountry
        phoneArrow = binding.crImageviewPhone
        faxArrow = binding.crImageviewFax

        setupArrowsForEachField()

        createContactViewModelProvider.newContact.observe(viewLifecycleOwner) {
            customerIdView.setText(it.customerId)
            companyNameView.setText(it.companyName)
            contactNameView.setText(it.contactName)
            contactTitleView.setText(it.contactTitle)
            addressView.setText(it.address)
            cityView.setText(it.city)
            emailView.setText(it.email)
            postalCodeView.setText(it.postalCode)
            countryView.setText(it.country)
            phoneView.setText(it.phone)
            faxView.setText(it.fax)
        }

        // if a passed contact was in bundle, reset layout using that contact
        if (savedInstanceState != null) {
            var bundledContact: Contact? = null
            bundledContact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getSerializable(keyBundleContact, Contact::class.java)
            } else {
                savedInstanceState.getSerializable(keyBundleContact) as? Contact
            }
            if (bundledContact != null) createContactViewModel.resetContact(bundledContact)
        }


        // add custom menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            this, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
        return root
    }

    private fun addContactClicked() {
        val newContact: Contact? = createContactViewModel.newContact.value
        if (newContact != null && Contact.isValidContact(newContact)) {

            // check for duplicate, if exists, ask user if they want to update or create duplicate
            if (contactTable.doesContactWithCustomerIdExist(newContact.customerId)) {
                // display dialogue asking user if they want to insert duplicate contact or update
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialogue_3_options)
                val title = dialog.findViewById(R.id.custom_dialogue_title) as TextView
                val body = dialog.findViewById(R.id.custom_dialogue_body) as TextView
                title.text = getString(R.string.dialogue_duplicate_contact)
                body.text = getString(R.string.dialogue_ask_to_update_or_create_duplicate)
                val updateBtn = dialog.findViewById(R.id.btn_1) as Button
                val duplicateBtn = dialog.findViewById(R.id.btn_2) as Button
                val dismissBtn = dialog.findViewById(R.id.btn_3) as Button
                updateBtn.setOnClickListener {
                    dialog.dismiss()
                    addContactToDatabase(newContact, false)
                }
                duplicateBtn.setOnClickListener {
                    dialog.dismiss()
                    addContactToDatabase(newContact, true)
                }
                dismissBtn.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }

            else {
                addContactToDatabase(newContact, false)
            }
        }
    }

    private fun addContactToDatabase(contact: Contact, duplicate: Boolean) {
        // perform database operation
        val dbOperationSuccessful =
            if (duplicate) contactTable.insertDuplicateContact(contact)
            else contactTable.addOrUpdateContact(contact)


        // proceed with notifying user if database operation was successful
        if (dbOperationSuccessful) {
            // reset new contact details
            createContactViewModel.resetContact(Contact.getEmptyContact())

            // display dialogue asking user if they want to go to the contact list to see the new contact
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialogue_2_options)
            val title = dialog.findViewById(R.id.custom_dialogue_title) as TextView
            val body = dialog.findViewById(R.id.custom_dialogue_body) as TextView
            title.text = getString(R.string.dialogue_added_contact)
            body.text = getString(R.string.dialogue_added_nav_ask)
            val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
            val noBtn = dialog.findViewById(R.id.btn_no) as Button
            yesBtn.setOnClickListener {
                dialog.dismiss()

                // navigate to contact list fragment
                val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.nav_contact_list)
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean = when(item.itemId) {

        R.id.menu_action_add_contact -> {
            view?.let { activity?.hideKeyboard(it) }
            addContactClicked()
            true
        }
        R.id.menu_action_add_reset -> {
            view?.let { activity?.hideKeyboard(it) }
            createContactViewModel.resetContact(Contact.getEmptyContact())
            true
        }
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

    // set arrows to show red if field is empty, green if field has text
    private fun setupArrowsForEachField() {
        customerIdView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                customerIdArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.customerId = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        companyNameView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                companyNameArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.companyName = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        contactNameView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                contactNameArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.contactName = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        contactTitleView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                contactTitleArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.contactTitle = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        addressView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                addressArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.address = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        cityView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                cityArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.city = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        emailView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                emailArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.email = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        postalCodeView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                postalCodeArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.postalCode = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        countryView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                countryArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.country = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        phoneView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                phoneArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.phone = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        faxView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val res = if(s.toString().isEmpty()) R.mipmap.image_red_arrow_foreground else R.mipmap.image_green_arrow_foreground
                faxArrow.background = ResourcesCompat.getDrawable(resources, res, null)
                createContactViewModel.newContact.value?.fax = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}