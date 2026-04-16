package com.devcore.uat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class SessionManager(private val context: Context) {

    companion object {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        val SAVED_EMAIL = stringPreferencesKey("saved_email")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val USER_ID = intPreferencesKey("user_id")
    }

    suspend fun saveAuthToken(token: String, rememberMe: Boolean, email: String) {
        context.dataStore.edit { prefs ->
            prefs[JWT_TOKEN] = token
            prefs[REMEMBER_ME] = rememberMe
            if (rememberMe) {
                prefs[SAVED_EMAIL] = email
            } else {
                prefs.remove(SAVED_EMAIL)
            }
        }
    }

    /** Persiste localmente si el usuario activó la huella digital */
    suspend fun saveBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED] = enabled
        }
    }

    val authTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[JWT_TOKEN]
    }

    val userIdFlow: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    suspend fun saveUserId(id: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
        }
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMEMBER_ME] ?: false
    }

    val savedEmailFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SAVED_EMAIL]
    }

    /** true si el usuario habilitó la huella en Configuración */
    val biometricEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED] ?: false
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(JWT_TOKEN)
            // NO borrar BIOMETRIC_ENABLED ni SAVED_EMAIL si remember_me está activo
            val isRemember = prefs[REMEMBER_ME] ?: false
            if (!isRemember) {
                prefs.remove(SAVED_EMAIL)
            }
            // La preferencia de huella se mantiene para el próximo login
        }
    }
}
