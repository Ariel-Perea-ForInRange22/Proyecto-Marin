package com.devcore.uat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class SessionManager(private val context: Context) {

    companion object {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val BIO_TOKEN = stringPreferencesKey("bio_token")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        val SAVED_EMAIL = stringPreferencesKey("saved_email")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    suspend fun saveAuthToken(token: String, rememberMe: Boolean, email: String) {
        context.dataStore.edit { prefs ->
            prefs[JWT_TOKEN] = token
            prefs[REMEMBER_ME] = rememberMe
            if (prefs[BIOMETRIC_ENABLED] == true) {
                prefs[BIO_TOKEN] = token
            }
            if (rememberMe) {
                prefs[SAVED_EMAIL] = email
            } else {
                prefs.remove(SAVED_EMAIL)
            }
        }
    }

    val authTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[JWT_TOKEN]
    }

    val bioTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[BIO_TOKEN]
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMEMBER_ME] ?: false
    }

    val savedEmailFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SAVED_EMAIL]
    }

    val biometricEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED] ?: false
    }

    suspend fun saveBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED] = enabled
            if (enabled) {
                // If the user enables biometric, copy current token if exists
                val currentToken = prefs[JWT_TOKEN]
                if (currentToken != null) {
                    prefs[BIO_TOKEN] = currentToken
                }
            } else {
                prefs.remove(BIO_TOKEN)
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(JWT_TOKEN)
            val isRemember = prefs[REMEMBER_ME] ?: false
            if (!isRemember) {
                prefs.remove(SAVED_EMAIL)
            }
        }
    }
}
