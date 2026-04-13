package com.tuempresa.tuapp.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tuempresa.tuapp.domain.model.SavedAddress

object AddressStore {

    private const val PREFS_NAME = "dgary_saved_addresses"
    private const val KEY_ADDRESSES = "addresses"
    private val gson = Gson()

    fun getAddresses(context: Context): MutableList<SavedAddress> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_ADDRESSES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<SavedAddress>>() {}.type
        return gson.fromJson<MutableList<SavedAddress>>(json, type) ?: mutableListOf()
    }

    fun saveAddresses(context: Context, addresses: List<SavedAddress>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(addresses)
        prefs.edit().putString(KEY_ADDRESSES, json).apply()
    }

    fun getPrimaryAddress(context: Context): SavedAddress? {
        val addresses = getAddresses(context)
        return addresses.firstOrNull { it.esPrincipal } ?: addresses.firstOrNull()
    }
}
