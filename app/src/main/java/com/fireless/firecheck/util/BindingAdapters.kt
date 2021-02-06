package com.fireless.firecheck.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.ui.home.MaintenanceAdapter

enum class ApiStatus { LOADING, ERROR, DONE, NO_CONTENT }

@BindingAdapter("listDataMaintenance")
fun bindRecyclerViewMaintenance(recyclerView: RecyclerView,
                         data: List<Maintenance>?) {

    val adapter = recyclerView.adapter as MaintenanceAdapter
    adapter.submitList(data)
}