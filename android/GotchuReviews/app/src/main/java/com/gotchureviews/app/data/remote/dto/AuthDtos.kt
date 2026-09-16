package com.gotchureviews.app.data.remote.dto

import com.gotchureviews.app.data.model.AppUser
import kotlinx.serialization.Serializable

@Serializable
data class GoogleSignInRequest(
    val idToken: String,
)

@Serializable
data class AuthResponse(
    val user: AppUser,
)
