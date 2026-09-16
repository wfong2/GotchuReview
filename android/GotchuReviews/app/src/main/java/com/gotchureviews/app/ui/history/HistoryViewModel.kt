package com.gotchureviews.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotchureviews.app.data.model.HistorySummary
import com.gotchureviews.app.data.model.VendorGroup
import com.gotchureviews.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class VendorSortOption {
    RECENT_FIRST,
    NAME,
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _vendors = MutableStateFlow<List<VendorGroup>>(emptyList())
    val vendors: StateFlow<List<VendorGroup>> = _vendors.asStateFlow()

    private val _summary = MutableStateFlow<HistorySummary?>(null)
    val summary: StateFlow<HistorySummary?> = _summary.asStateFlow()

    private val _creditBalance = MutableStateFlow(0)
    val creditBalance: StateFlow<Int> = _creditBalance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortOption = MutableStateFlow(VendorSortOption.RECENT_FIRST)
    val sortOption: StateFlow<VendorSortOption> = _sortOption.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var hasLoadedOnce = false

    val sortedVendors: List<VendorGroup>
        get() = when (_sortOption.value) {
            VendorSortOption.RECENT_FIRST ->
                _vendors.value.sortedByDescending { it.latestInvoiceDate }
            VendorSortOption.NAME ->
                _vendors.value.sortedBy {
                    (it.contractor.businessName.takeIf { n -> n.isNotEmpty() } ?: it.contractor.name)
                        .lowercase()
                }
        }

    fun setSortOption(option: VendorSortOption) {
        _sortOption.value = option
    }

    fun load() {
        viewModelScope.launch {
            if (!hasLoadedOnce) {
                _isLoading.value = true
            }
            _errorMessage.value = null

            try {
                val response = userRepository.getVendorHistory()
                _vendors.value = response.vendors
                _summary.value = response.summary
                _creditBalance.value = userRepository.getCreditBalance()
                hasLoadedOnce = true
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }

            _isLoading.value = false
        }
    }
}
