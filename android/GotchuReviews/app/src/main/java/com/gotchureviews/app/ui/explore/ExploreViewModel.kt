package com.gotchureviews.app.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotchureviews.app.data.model.Contractor
import com.gotchureviews.app.data.model.TradeCategory
import com.gotchureviews.app.data.repository.ContractorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val contractorRepository: ContractorRepository,
) : ViewModel() {

    private val _contractors = MutableStateFlow<List<Contractor>>(emptyList())
    val contractors: StateFlow<List<Contractor>> = _contractors.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedCategory = MutableStateFlow<TradeCategory?>(null)
    val selectedCategory: StateFlow<TradeCategory?> = _selectedCategory.asStateFlow()

    private val _zipCode = MutableStateFlow("")
    val zipCode: StateFlow<String> = _zipCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _totalResults = MutableStateFlow(0)
    val totalResults: StateFlow<Int> = _totalResults.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun updateZipCode(zip: String) {
        _zipCode.value = zip
    }

    fun search() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = contractorRepository.search(
                    query = _searchText.value.takeIf { it.isNotBlank() },
                    category = _selectedCategory.value?.apiValue,
                    zipCode = _zipCode.value.takeIf { it.isNotBlank() },
                )
                _contractors.value = response.contractors
                _totalResults.value = response.total
                _hasSearched.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                _hasSearched.value = true
            }
            _isLoading.value = false
        }
    }

    fun selectCategory(category: TradeCategory) {
        _selectedCategory.value = category
        search()
    }

    fun loadNearby() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = contractorRepository.search(
                    zipCode = _zipCode.value.takeIf { it.isNotBlank() },
                    limit = 10,
                )
                _contractors.value = response.contractors
                _totalResults.value = response.total
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
            _isLoading.value = false
        }
    }
}
