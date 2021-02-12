package com.fireless.firecheck.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fireless.firecheck.databinding.MaintenanceItemBinding
import com.fireless.firecheck.models.MaintenanceProperty

class MaintenanceViewHolder(
        private val binding: MaintenanceItemBinding,
        listener: MaintenanceAdapter.MaintenanceAdapterListener
): RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

    init {
        binding.run {
            this.listener = listener
        }
    }

    fun bind(maintenance: MaintenanceProperty) {
        binding.maintenance = maintenance
        binding.executePendingBindings()
    }

    override val reboundableView: View
        get() = TODO("Not yet implemented")

    override fun onReboundOffsetChanged(currentSwipePercentage: Float, swipeThreshold: Float, currentTargetHasMetThresholdOnce: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRebounded() {
        TODO("Not yet implemented")
    }


}