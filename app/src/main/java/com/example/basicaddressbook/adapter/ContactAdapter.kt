package com.example.basicaddressbook.adapter

import android.graphics.PorterDuff
import com.example.basicaddressbook.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.basicaddressbook.databinding.CellContactBinding
import com.example.basicaddressbook.model.Contact

class ContactAdapter(
    list: List<Contact>?,
    private val itemClickListener: ItemClickListener,
    private val emailClickListener: EmailClickListener,
    private val addressClickListener: AddressClickListener,
    private val phoneClickListener: PhoneClickListener) :
    ListAdapter<Contact, ContactAdapter.ViewHolder>(DiffCallback())
{
    private var contactList: List<Contact>? = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_contact, parent, false)
        return ViewHolder(itemLayout)
    }

    fun setNewList(_contactList: List<Contact>?) {
        contactList = _contactList
        submitList(contactList)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CellContactBinding.bind(itemView)

        private val contactCardView = binding.cardViewMain
        private val customerIdTextView = binding.cellTextviewCustomerId
        private val companyNameTextView = binding.cellTextviewCompanyName
        private val contactNameTextView = binding.cellTextviewContactName
        private val contactTitleTextView = binding.cellTextviewContactTitle
        private val cityTextView = binding.cellTextviewCity
        private val postalCodeTextView = binding.cellTextviewPostalcode
        private val countryTextView = binding.cellTextviewCountry
        private val faxTextView = binding.cellTextviewFax

        // These views will be selectable to perform certain actions, need to be public
        val addressTextView = binding.cellTextviewAddress
        val emailTextView = binding.cellTextviewEmail
        val phoneTextView = binding.cellTextviewPhone

        fun bind(contact: Contact) {
            // account for card being selected
            if (contact.selected == 0) contactCardView.backgroundTintMode = PorterDuff.Mode.CLEAR
            else contactCardView.backgroundTintMode = PorterDuff.Mode.DARKEN

            customerIdTextView.text = contact.customerId
            companyNameTextView.text = contact.companyName
            contactNameTextView.text = contact.contactName
            contactTitleTextView.text = contact.contactTitle
            addressTextView.text = contact.address
            cityTextView.text = contact.city
            emailTextView.text = contact.email
            postalCodeTextView.text = contact.postalCode
            countryTextView.text = contact.country
            phoneTextView.text = contact.phone
            faxTextView.text = contact.fax
        }

    }

    interface ItemClickListener {
        fun onItemClick(contact: Contact, position: Int)
    }
    interface EmailClickListener {
        fun onEmailClick(contact: Contact, position: Int)
    }
    interface AddressClickListener {
        fun onAddressClick(contact: Contact, position: Int)
    }
    interface PhoneClickListener {
        fun onPhoneClick(contact: Contact, position: Int)
    }

    override fun getItemCount(): Int {
        return contactList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (contactList != null) {
            contactList!![position].let { contactList ->
                holder.bind(contactList)

                holder.itemView.setOnClickListener {
                    // animate tap
                    val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_card_tapped)
                    holder.itemView.startAnimation(animation)

                    itemClickListener.onItemClick(contactList, position)
                }

                holder.addressTextView.setOnClickListener {
                    // animate tap
                    val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_card_tapped)
                    holder.addressTextView.startAnimation(animation)

                    addressClickListener.onAddressClick(contactList, position)
                }

                holder.emailTextView.setOnClickListener {
                    // animate tap
                    val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_card_tapped)
                    holder.emailTextView.startAnimation(animation)

                    emailClickListener.onEmailClick(contactList, position)
                }

                holder.phoneTextView.setOnClickListener {
                    // animate tap
                    val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_card_tapped)
                    holder.phoneTextView.startAnimation(animation)

                    phoneClickListener.onPhoneClick(contactList, position)
                }
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.customerId == newItem.customerId
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}
