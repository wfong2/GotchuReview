package com.gotchureviews.app.util

import java.text.NumberFormat
import java.util.Locale

fun Int.formatCurrency(): String {
    val fmt = NumberFormat.getIntegerInstance(Locale.US)
    return "$${fmt.format(this)}"
}

fun Double.formatCurrency(): String {
    return this.toLong().toInt().formatCurrency()
}

fun String.formatInvoiceDate(): String {
    // Convert "2024-03-15" to "2024/03/15"
    return this.take(10).replace("-", "/")
}

fun String.formatShortDate(): String {
    // Convert "2024-03-15" to "2024/03"
    return this.take(7).replace("-", "/")
}
