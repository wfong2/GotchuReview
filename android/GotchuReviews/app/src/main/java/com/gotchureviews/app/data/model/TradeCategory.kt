package com.gotchureviews.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImagesearchRoller
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.ui.graphics.vector.ImageVector
import com.gotchureviews.app.R

enum class TradeCategory(
    val apiValue: String,
    val displayNameRes: Int,
    val icon: ImageVector,
) {
    PLUMBER("plumber", R.string.category_plumber, Icons.Filled.Plumbing),
    ELECTRICIAN("electrician", R.string.category_electrician, Icons.Filled.ElectricBolt),
    ROOFER("roofer", R.string.category_roofer, Icons.Filled.Home),
    PAINTER("painter", R.string.category_painter, Icons.Filled.ImagesearchRoller),
    HVAC("hvac", R.string.category_hvac, Icons.Filled.LocalFireDepartment),
    GENERAL("general", R.string.category_general, Icons.Filled.Build),
    LANDSCAPER("landscaper", R.string.category_landscaper, Icons.Filled.Grass),
    CARPENTER("carpenter", R.string.category_carpenter, Icons.Filled.Carpenter),
    MASON("mason", R.string.category_mason, Icons.Filled.Foundation),
    FLOORING("flooring", R.string.category_flooring, Icons.Filled.GridOn);

    companion object {
        fun fromApiValue(value: String): TradeCategory? =
            entries.find { it.apiValue == value }
    }
}
