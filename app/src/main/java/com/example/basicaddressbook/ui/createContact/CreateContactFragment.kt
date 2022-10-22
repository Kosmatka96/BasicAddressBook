package com.example.basicaddressbook.ui.createContact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.basicaddressbook.databinding.FragmentCreateContactBinding

class CreateContactFragment : Fragment() {

private var _binding: FragmentCreateContactBinding? = null

  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val createContactViewModel =
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
      createContactViewModel.text.observe(viewLifecycleOwner) {
          customerIdTextView.text = it
          companyNameTextView.text = it
          contactNameTextView.text = it
          contactTitleTextView.text = it
          addressTextView.text = it
          cityTextView.text = it
          emailTextView.text = it
          postalCodeTextView.text = it
          countryTextView.text = it
          phoneTextView.text = it
          faxTextView.text = it
    }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}