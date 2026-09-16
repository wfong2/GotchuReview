package com.gotchureviews.app.data.remote

import com.gotchureviews.app.data.model.ExtractionResponse
import com.gotchureviews.app.data.model.VendorHistoryResponse
import com.gotchureviews.app.data.remote.dto.AuthResponse
import com.gotchureviews.app.data.remote.dto.BalanceResponse
import com.gotchureviews.app.data.remote.dto.ContractorDetailResponse
import com.gotchureviews.app.data.remote.dto.ContractorListResponse
import com.gotchureviews.app.data.remote.dto.GoogleSignInRequest
import com.gotchureviews.app.data.remote.dto.ReviewResponse
import com.gotchureviews.app.data.remote.dto.ReviewSubmission
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("auth/google")
    suspend fun signInWithGoogle(@Body body: GoogleSignInRequest): AuthResponse

    // Contractors
    @GET("contractors")
    suspend fun searchContractors(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null,
        @Query("zipCode") zipCode: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): ContractorListResponse

    @GET("contractors/{id}")
    suspend fun getContractor(@Path("id") id: String): ContractorDetailResponse

    // Invoice extraction
    @Multipart
    @POST("invoices/extract")
    suspend fun extractInvoice(@Part image: MultipartBody.Part): ExtractionResponse

    // Reviews
    @POST("reviews")
    suspend fun submitReview(@Body body: ReviewSubmission): ReviewResponse

    // User
    @GET("users/me")
    suspend fun getCurrentUser(): AuthResponse

    @GET("users/me/history")
    suspend fun getVendorHistory(@Query("groupBy") groupBy: String = "vendor"): VendorHistoryResponse

    // Credits
    @GET("credits/balance")
    suspend fun getCreditBalance(): BalanceResponse
}
