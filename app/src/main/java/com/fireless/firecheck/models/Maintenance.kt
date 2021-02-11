/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fireless.firecheck.models

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json

/**
 * A simple data class to represent an Maintenance.
 */
data class Maintenance(
    val id: Int,
    @Json(name = "date_of_control") val dateOfControl: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "extinguisher_id") val extinguisherId: String)

object MaintenanceDiffCallback : DiffUtil.ItemCallback<MaintenanceProperty>() {
    override fun areItemsTheSame(oldItem: MaintenanceProperty, newItem: MaintenanceProperty) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MaintenanceProperty, newItem: MaintenanceProperty) = oldItem == newItem
}


