package com.gotchureviews.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val balance: Int,
)
