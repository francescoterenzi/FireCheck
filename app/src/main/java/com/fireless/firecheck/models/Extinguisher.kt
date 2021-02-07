package com.fireless.firecheck.models

import com.squareup.moshi.Json

/**
 * A simple data class to represent a Extinguisher.
 */
data class Extinguisher(
    val id: String,
    val weight: String,
    val typology: String,
    @Json(name = "company_id") val companyId: String)