package com.gotchureviews.app.ui.onboarding

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.gotchureviews.app.data.local.TokenStore
import com.gotchureviews.app.data.model.AppUser
import com.gotchureviews.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore,
) : ViewModel() {

    companion object {
        private const val GOOGLE_CLIENT_ID =
            "689036353712-1do1c716605eh0ivss1dauter73v6mjc.apps.googleusercontent.com"
    }

    val hasCompletedOnboarding: StateFlow<Boolean> = tokenStore.hasCompletedOnboarding
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.restoreSession()
            if (user != null) {
                _currentUser.value = user
                _isSignedIn.value = true
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(GOOGLE_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val user = authRepository.signInWithGoogle(googleIdToken)
                _currentUser.value = user
                _isSignedIn.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Sign-in failed"
            }

            _isLoading.value = false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            authRepository.setOnboardingCompleted()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _isSignedIn.value = false
        }
    }

    fun refreshUser() {
        viewModelScope.launch {
            try {
                val user = authRepository.restoreSession()
                if (user != null) {
                    _currentUser.value = user
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }
}
