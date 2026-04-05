package com.tuempresa.tuapp.data.local

import android.content.Context

/**
 * Persistencia simple de sesión para guardar el token de API.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "dgary_session"
        private const val KEY_TOKEN = "api_token"
    }
}
