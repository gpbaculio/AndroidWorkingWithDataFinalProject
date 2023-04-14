package com.example.littlelemon

import kotlinx.serialization.Serializable



@Serializable
data class MenuNetwork(
    // add code here
    val menu: List<MenuItemNetwork>
)

@Serializable
data class MenuItemNetwork(
    // add code here
    val id: Int,
    val title: String,
    val price: String
) {
    fun toMenuItemRoom() = MenuItemRoom(
        // add code here
        id = id,
        title = title,
        price = price.toDoubleOrNull() ?: 0.0
    )
}
