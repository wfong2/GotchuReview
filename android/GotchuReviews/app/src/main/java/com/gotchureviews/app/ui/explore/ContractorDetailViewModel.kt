package com.gotchureviews.app.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotchureviews.app.data.model.Contractor
import com.gotchureviews.app.data.model.Review
import com.gotchureviews.app.data.repository.ContractorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContractorDetailViewModel @Inject constructor(
    private val contractorRepository: ContractorRepository,
) : ViewModel() {

    private val _contractor = MutableStateFlow<Contractor?>(null)
    val contractor: StateFlow<Contractor?> = _contractor.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun load(contractorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = contractorRepository.getDetail(contractorId)
                _contractor.value = response.contractor
                _reviews.value = response.reviews
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
            _isLoading.value = false
        }
    }
}
