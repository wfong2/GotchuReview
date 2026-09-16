package com.gotchureviews.app.ui.scan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotchureviews.app.data.model.ExtractionResponse
import com.gotchureviews.app.data.model.TradeCategory
import com.gotchureviews.app.data.remote.dto.InvoiceData
import com.gotchureviews.app.data.remote.dto.NewContractor
import com.gotchureviews.app.data.remote.dto.Ratings
import com.gotchureviews.app.data.remote.dto.ReviewSubmission
import com.gotchureviews.app.data.repository.InvoiceRepository
import com.gotchureviews.app.data.repository.ReviewRepository
import com.gotchureviews.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

enum class ScanFlowStep {
    CAMERA,
    EXTRACTING,
    EXTRACTED,
    REVIEW,
    SUBMITTED,
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    private val _extractionResult = MutableStateFlow<ExtractionResponse?>(null)
    val extractionResult: StateFlow<ExtractionResponse?> = _extractionResult.asStateFlow()

    private val _isExtracting = MutableStateFlow(false)
    val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Review form state
    private val _selectedContractorId = MutableStateFlow<String?>(null)
    val selectedContractorId: StateFlow<String?> = _selectedContractorId.asStateFlow()

    private val _isNewContractor = MutableStateFlow(false)
    val isNewContractor: StateFlow<Boolean> = _isNewContractor.asStateFlow()

    private val _newContractorCategory = MutableStateFlow(TradeCategory.GENERAL)
    val newContractorCategory: StateFlow<TradeCategory> = _newContractorCategory.asStateFlow()

    private val _ratingQuality = MutableStateFlow(0)
    val ratingQuality: StateFlow<Int> = _ratingQuality.asStateFlow()

    private val _ratingCommunication = MutableStateFlow(0)
    val ratingCommunication: StateFlow<Int> = _ratingCommunication.asStateFlow()

    private val _ratingTimeliness = MutableStateFlow(0)
    val ratingTimeliness: StateFlow<Int> = _ratingTimeliness.asStateFlow()

    private val _ratingValue = MutableStateFlow(0)
    val ratingValue: StateFlow<Int> = _ratingValue.asStateFlow()

    private val _reviewTitle = MutableStateFlow("")
    val reviewTitle: StateFlow<String> = _reviewTitle.asStateFlow()

    private val _reviewBody = MutableStateFlow("")
    val reviewBody: StateFlow<String> = _reviewBody.asStateFlow()

    private val _flowStep = MutableStateFlow(ScanFlowStep.CAMERA)
    val flowStep: StateFlow<ScanFlowStep> = _flowStep.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _creditBalance = MutableStateFlow<Int?>(null)
    val creditBalance: StateFlow<Int?> = _creditBalance.asStateFlow()

    fun setCapturedBitmap(bitmap: Bitmap?) {
        _capturedBitmap.value = bitmap
    }

    fun setSelectedContractorId(id: String?) {
        _selectedContractorId.value = id
        _isNewContractor.value = false
    }

    fun setIsNewContractor(isNew: Boolean) {
        _isNewContractor.value = isNew
        if (isNew) _selectedContractorId.value = null
    }

    fun setNewContractorCategory(category: TradeCategory) {
        _newContractorCategory.value = category
    }

    fun setRatingQuality(rating: Int) { _ratingQuality.value = rating }
    fun setRatingCommunication(rating: Int) { _ratingCommunication.value = rating }
    fun setRatingTimeliness(rating: Int) { _ratingTimeliness.value = rating }
    fun setRatingValue(rating: Int) { _ratingValue.value = rating }
    fun setReviewTitle(title: String) { _reviewTitle.value = title }
    fun setReviewBody(body: String) { _reviewBody.value = body }

    fun clearError() { _errorMessage.value = null }

    val canSubmitReview: Boolean
        get() = _ratingQuality.value > 0 &&
                _ratingCommunication.value > 0 &&
                _ratingTimeliness.value > 0 &&
                _ratingValue.value > 0 &&
                _reviewTitle.value.isNotBlank() &&
                (_selectedContractorId.value != null || _isNewContractor.value)

    fun extractInvoice(data: ByteArray, mimeType: String = "image/jpeg") {
        viewModelScope.launch {
            _flowStep.value = ScanFlowStep.EXTRACTING
            _isExtracting.value = true
            _errorMessage.value = null

            try {
                val result = invoiceRepository.extractInvoice(data, mimeType)
                _extractionResult.value = result

                // Auto-select top contractor match if confidence > 70%
                val topMatch = result.contractorMatches.firstOrNull()
                if (topMatch != null && topMatch.confidence > 70) {
                    _selectedContractorId.value = topMatch.contractor.id
                }

                _flowStep.value = ScanFlowStep.EXTRACTED
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    _errorMessage.value = "This invoice has already been submitted."
                } else {
                    _errorMessage.value = e.localizedMessage
                }
                _flowStep.value = ScanFlowStep.CAMERA
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                _flowStep.value = ScanFlowStep.CAMERA
            }

            _isExtracting.value = false
        }
    }

    fun confirmExtraction() {
        _flowStep.value = ScanFlowStep.REVIEW
    }

    fun rescan() {
        _capturedBitmap.value = null
        _extractionResult.value = null
        _errorMessage.value = null
        _flowStep.value = ScanFlowStep.CAMERA
    }

    fun submitReview() {
        val result = _extractionResult.value ?: return

        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            val extracted = result.extracted

            val submission = ReviewSubmission(
                contractorId = if (_isNewContractor.value) null else _selectedContractorId.value,
                newContractor = if (_isNewContractor.value) NewContractor(
                    name = extracted.contractorName,
                    businessName = extracted.businessName,
                    category = _newContractorCategory.value.apiValue,
                    phone = extracted.contractorPhone,
                    email = extracted.contractorEmail,
                    zipCode = extracted.zipCode,
                ) else null,
                invoiceData = InvoiceData(
                    totalAmount = extracted.totalAmount,
                    currency = extracted.currency,
                    laborCost = extracted.laborCost,
                    materialsCost = extracted.materialsCost,
                    hourlyRate = extracted.hourlyRate,
                    projectDuration = extracted.projectDuration,
                    invoiceDate = extracted.invoiceDate,
                    description = extracted.description,
                    zipCode = extracted.zipCode,
                    lineItems = extracted.lineItems,
                ),
                documentHash = result.documentHash,
                ratings = Ratings(
                    quality = _ratingQuality.value,
                    communication = _ratingCommunication.value,
                    timeliness = _ratingTimeliness.value,
                    value = _ratingValue.value,
                ),
                title = _reviewTitle.value,
                body = _reviewBody.value,
                workType = extracted.description,
                estimatedBreakdown = result.estimatedBreakdown,
            )

            try {
                val response = reviewRepository.submitReview(submission)
                _creditBalance.value = response.creditBalance
                _flowStep.value = ScanFlowStep.SUBMITTED
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }

            _isSubmitting.value = false
        }
    }

    fun reset() {
        _capturedBitmap.value = null
        _extractionResult.value = null
        _isExtracting.value = false
        _errorMessage.value = null
        _selectedContractorId.value = null
        _isNewContractor.value = false
        _newContractorCategory.value = TradeCategory.GENERAL
        _ratingQuality.value = 0
        _ratingCommunication.value = 0
        _ratingTimeliness.value = 0
        _ratingValue.value = 0
        _reviewTitle.value = ""
        _reviewBody.value = ""
        _creditBalance.value = null
        _isSubmitting.value = false
        _flowStep.value = ScanFlowStep.CAMERA
    }
}
