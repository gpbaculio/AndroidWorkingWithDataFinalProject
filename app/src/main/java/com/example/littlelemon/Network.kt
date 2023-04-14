package com.example.littlelemon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuNetwork(
    // add code here
)

@Serializable
data class MenuItemNetwork(
    // add code here
) {
    fun toMenuItemRoom() = MenuItemRoom(
        // add code here
    )
}
