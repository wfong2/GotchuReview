package com.gotchureviews.app.data.repository

import com.gotchureviews.app.data.remote.ApiService
import com.gotchureviews.app.data.remote.dto.ReviewResponse
import com.gotchureviews.app.data.remote.dto.ReviewSubmission
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun submitReview(submission: ReviewSubmission): ReviewResponse {
        return apiService.submitReview(submission)
    }
}
