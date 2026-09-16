package com.gotchureviews.app.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseAuthApi {

    @POST("v1/accounts:signInWithIdp")
    suspend fun signInWithIdp(
        @Query("key") apiKey: String,
        @Body body: FirebaseIdpRequest,
    ): FirebaseIdpResponse

    @FormUrlEncoded
    @POST("v1/token")
    suspend fun refreshToken(
        @Query("key") apiKey: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
    ): FirebaseRefreshResponse
}

@Serializable
data class FirebaseIdpRequest(
    val postBody: String,
    val requestUri: String = "http://localhost",
    val returnIdpCredential: Boolean = true,
    val returnSecureToken: Boolean = true,
)

@Serializable
data class FirebaseIdpResponse(
    val idToken: String,
    val refreshToken: String,
    val localId: String? = null,
    val email: String? = null,
    val displayName: String? = null,
)

@Serializable
data class FirebaseRefreshResponse(
    val id_token: String,
    val refresh_token: String,
    val expires_in: String? = null,
)
