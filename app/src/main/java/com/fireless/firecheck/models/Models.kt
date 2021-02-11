package com.fireless.firecheck.models

import com.squareup.moshi.Json

data class UserProperty(
        val id: String,
        @Json(name = "first_name") val firstName: String,
        @Json(name = "last_name") val lastName: String
)

data class MaintenanceProperty(
        val id: String,
        @Json(name = "date_of_control") val dateOfControl: String,
        @Json(name = "user_id") val userId: String,
        @Json(name = "extinguisher_id") val extinguisherId: String)