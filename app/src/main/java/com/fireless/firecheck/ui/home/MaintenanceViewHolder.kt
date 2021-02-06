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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.databinding.MaintenanceItemBinding


class MaintenanceViewHolder(
    private val binding: MaintenanceItemBinding,
    listener: MaintenanceAdapter.MaintenanceAdapterListener
): RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

    init {
        binding.run {
            this.listener = listener
        }
    }

    fun bind(maintenance: Maintenance) {
        binding.maintenance = maintenance
        binding.executePendingBindings()
    }

    override val reboundableView: View
        get() = TODO("Not yet implemented")

    override fun onReboundOffsetChanged(
        currentSwipePercentage: Float,
        swipeThreshold: Float,
        currentTargetHasMetThresholdOnce: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun onRebounded() {
        TODO("Not yet implemented")
    }

}