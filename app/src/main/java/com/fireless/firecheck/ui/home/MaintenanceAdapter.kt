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

package com.fireless.firecheck.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.models.MaintenanceDiffCallback
import com.fireless.firecheck.databinding.MaintenanceItemBinding

/**
 * Simple adapter to display Email's in MainActivity.
 */
class MaintenanceAdapter(
    private val listener: MaintenanceAdapterListener
) : ListAdapter<Maintenance, MaintenanceViewHolder>(MaintenanceDiffCallback) {

    interface MaintenanceAdapterListener {
        fun onMaintenanceClicked(cardView: View, maintenance: Maintenance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        return MaintenanceViewHolder(
            MaintenanceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}