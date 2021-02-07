package com.fireless.firecheck.ui.maintenance

import android.util.Log
import com.fireless.firecheck.models.Company
import com.fireless.firecheck.models.Extinguisher
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.network.CompanyApi
import com.fireless.firecheck.network.ExtinguisherApi
import com.fireless.firecheck.network.MaintenanceApi
import com.fireless.firecheck.util.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "MAINT VIEW MODEL"

class MaintenanceViewModel() : ViewModel() {

    private val _companyId = MutableLiveData<String>()
    val companyId: LiveData<String>
        get() = _companyId

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company>
        get() = _company

    private val _extinguisher = MutableLiveData<Extinguisher>()
    val extinguisher: LiveData<Extinguisher>
        get() = _extinguisher

    private val _maintenance = MutableLiveData<Maintenance>()
    val maintenance: LiveData<Maintenance>
        get() = _maintenance

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private var _isNetworkErrorShown: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    fun loadInfo(id: Int) {
        coroutineScope.launch {
            val getPropertiesDeferred = MaintenanceApi
                .retrofitServiceGetMaintenance
                .getMaintenance(id)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _maintenance.value = listResult

                loadExtinguisher(_maintenance.value!!.extinguisherId)

                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }

    private fun loadExtinguisher(id: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = ExtinguisherApi
                .retrofitServiceGetExtinguisher
                .getExtinguisher(id)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _extinguisher.value = listResult

                loadCompany(_extinguisher.value!!.companyId)

                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }

    private fun loadCompany(id: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = CompanyApi
                .retrofitServiceGetCompany
                .getCompany(id)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _company.value = listResult
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }

}

