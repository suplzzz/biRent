package com.example.birent.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("birent_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_ID_KEY = longPreferencesKey("user_id")

    // Null означает гостя
    val userId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    suspend fun saveUser(id: Long) {
        context.dataStore.edit { it[USER_ID_KEY] = id }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.remove(USER_ID_KEY) }
    }
}