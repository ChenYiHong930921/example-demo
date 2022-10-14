package com.chenyihong.exampledemo.androidapi.setting

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat

class HelperFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        val qaPreferenceCategory = PreferenceCategory(context)
        qaPreferenceCategory.key = "qa"
        qaPreferenceCategory.title = "QA"
        val qa1Preference = Preference(context)
        qa1Preference.key = "qa1"
        qa1Preference.title = "How to open Notification"
        qa1Preference.summary = "Open system setting"
        val qa2Preference = Preference(context)
        qa2Preference.key = "qa2"
        qa2Preference.title = "How to bind google account"
        qa2Preference.summary = "oOpen settings, bind Google login"
        screen.addPreference(qaPreferenceCategory)
        qaPreferenceCategory.addPreference(qa1Preference)
        qaPreferenceCategory.addPreference(qa2Preference)
        val contactPreferenceCategory = PreferenceCategory(context)
        contactPreferenceCategory.key = "contact"
        contactPreferenceCategory.title = "Contact"
        val onlineContactPreference = Preference(context)
        onlineContactPreference.key = "online_customer"
        onlineContactPreference.title = "Online customer"
        onlineContactPreference.summary = "Contact online customer service to solve your problem"
        onlineContactPreference.setOnPreferenceClickListener {
            requireActivity().runOnUiThread { Toast.makeText(requireContext(), "Click online customer", Toast.LENGTH_SHORT).show() }
            true
        }
        val telephoneContactPreference = Preference(context)
        telephoneContactPreference.key = "telephone_customer"
        telephoneContactPreference.title = "Telephone customer"
        telephoneContactPreference.summary = "Contact telephone_contact customer service to solve your problem"
        telephoneContactPreference.setOnPreferenceClickListener {
            requireActivity().runOnUiThread { Toast.makeText(requireContext(), "Click Telephone customer", Toast.LENGTH_SHORT).show() }
            true
        }
        screen.addPreference(contactPreferenceCategory)
        contactPreferenceCategory.addPreference(onlineContactPreference)
        contactPreferenceCategory.addPreference(telephoneContactPreference)
        preferenceScreen = screen
    }
}