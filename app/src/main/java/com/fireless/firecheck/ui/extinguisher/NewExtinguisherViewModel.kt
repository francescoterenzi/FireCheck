package com.fireless.firecheck.ui.extinguisher

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.network.ExtinguisherApi
import com.fireless.firecheck.util.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "NEW EXT VIEW MODEL"

class NewExtinguisherViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    val extinguisherId = ""
    val companyId = 1

    fun createExtinguisher(view: View) {
        coroutineScope.launch {
            ExtinguisherApi
                .retrofitServiceSetExtinguisher
                .setExtinguisher(extinguisherId, companyId)
            try {
                _status.value = ApiStatus.LOADING
                _status.value = ApiStatus.DONE
                view.findNavController().navigate(R.id.action_newExtinguisherFragment_to_homeFragment)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
            }
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


}

