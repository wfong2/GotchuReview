package com.gotchureviews.app.data.repository

import com.gotchureviews.app.data.local.TokenStore
import com.gotchureviews.app.data.model.AppUser
import com.gotchureviews.app.data.remote.ApiService
import com.gotchureviews.app.data.remote.FirebaseAuthApi
import com.gotchureviews.app.data.remote.FirebaseIdpRequest
import com.gotchureviews.app.data.remote.dto.GoogleSignInRequest
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuthApi: FirebaseAuthApi,
    @Named("firebaseTokenApi") private val firebaseTokenApi: FirebaseAuthApi,
    private val tokenStore: TokenStore,
) {
    companion object {
        const val FIREBASE_API_KEY = "AIzaSyAqGLmvZy2agbEX9UGnGuDeQo05c6m0c3g"
    }

    /**
     * Exchange Google ID token for Firebase tokens, then authenticate with backend.
     */
    suspend fun signInWithGoogle(googleIdToken: String): AppUser {
        // Step 1: Exchange Google ID token for Firebase tokens
        val firebaseResponse = firebaseAuthApi.signInWithIdp(
            apiKey = FIREBASE_API_KEY,
            body = FirebaseIdpRequest(
                postBody = "id_token=$googleIdToken&providerId=google.com",
            ),
        )

        // Step 2: Persist Firebase tokens
        tokenStore.saveTokens(firebaseResponse.idToken, firebaseResponse.refreshToken)

        // Step 3: Authenticate with our backend
        val authResponse = apiService.signInWithGoogle(
            GoogleSignInRequest(idToken = firebaseResponse.idToken),
        )
        val user = authResponse.user
        tokenStore.saveUser(user)

        return user
    }

    /**
     * Refresh Firebase token using stored refresh token.
     */
    suspend fun refreshFirebaseToken(): Boolean {
        val refreshToken = tokenStore.refreshToken.firstOrNull() ?: return false
        return try {
            val response = firebaseTokenApi.refreshToken(
                apiKey = FIREBASE_API_KEY,
                refreshToken = refreshToken,
            )
            tokenStore.saveTokens(response.id_token, response.refresh_token)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Restore session from stored tokens + cached user.
     * Attempts a token refresh and user fetch in the background.
     */
    suspend fun restoreSession(): AppUser? {
        val token = tokenStore.authToken.firstOrNull() ?: return null
        val cachedUser = tokenStore.cachedUser.firstOrNull()

        // Try to refresh token and fetch fresh user
        try {
            if (refreshFirebaseToken()) {
                val freshUser = apiService.getCurrentUser().user
                tokenStore.saveUser(freshUser)
                return freshUser
            }
        } catch (_: Exception) {
            // Fall back to cached user
        }

        return cachedUser
    }

    suspend fun signOut() {
        tokenStore.clear()
    }

    suspend fun setOnboardingCompleted() {
        tokenStore.setOnboardingCompleted()
    }
}
