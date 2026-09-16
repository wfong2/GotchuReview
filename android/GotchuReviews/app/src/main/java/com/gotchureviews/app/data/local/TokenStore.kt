package com.gotchureviews.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gotchureviews.app.data.model.AppUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val CACHED_USER = stringPreferencesKey("cached_user")
        private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val authToken: Flow<String?> = dataStore.data.map { it[AUTH_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[REFRESH_TOKEN] }
    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { it[HAS_COMPLETED_ONBOARDING] ?: false }

    val cachedUser: Flow<AppUser?> = dataStore.data.map { prefs ->
        prefs[CACHED_USER]?.let {
            try {
                json.decodeFromString<AppUser>(it)
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun saveTokens(authToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = authToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun saveUser(user: AppUser) {
        dataStore.edit { prefs ->
            prefs[CACHED_USER] = json.encodeToString(AppUser.serializer(), user)
        }
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[HAS_COMPLETED_ONBOARDING] = true }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(CACHED_USER)
            prefs.remove(HAS_COMPLETED_ONBOARDING)
        }
    }
}
