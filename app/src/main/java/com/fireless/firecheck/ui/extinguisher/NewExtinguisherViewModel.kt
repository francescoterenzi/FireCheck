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

    var extinguisherId = ""
    var weight = ""
    var typology = ""
    var companyId = ""

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _extinguisherIdError = MutableLiveData<String>()
    val extinguisherIdError: LiveData<String>
        get() = _extinguisherIdError

    private val _weightError = MutableLiveData<String>()
    val weightError: LiveData<String>
        get() = _weightError

    private val _typologyError = MutableLiveData<String>()
    val typologyError: LiveData<String>
        get() = _typologyError

    private val _companyIdError = MutableLiveData<String>()
    val companyIdError: LiveData<String>
        get() = _companyIdError

    init {
        _extinguisherIdError.value = ""
        _weightError.value = ""
        _typologyError.value = ""
        _companyIdError.value = ""
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private var _isNetworkErrorShown: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    fun createExtinguisher(view: View) {

        if (!isFormDataValid()) {
            return
        }

        coroutineScope.launch {
            val setPropertiesDeferred = ExtinguisherApi
                .retrofitServiceSetExtinguisher
                .setExtinguisher(extinguisherId, weight, typology, companyId)
            try {
                _status.value = ApiStatus.LOADING
                setPropertiesDeferred.await()
                _status.value = ApiStatus.DONE
                view.findNavController().navigate(R.id.action_newExtinguisherFragment_to_homeFragment)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    private fun isFormDataValid(): Boolean {
        var result = true

        _extinguisherIdError.value = null
        _typologyError.value = null
        _weightError.value = null
        _companyIdError.value = null

        if (extinguisherId.isEmpty() ||
                weight.isEmpty() ||
                typology.isEmpty() ||
                companyId.isEmpty()) {

            if (extinguisherId.isEmpty()) {
                _extinguisherIdError.value = "Please insert a valid extinguisherId"
                Log.d("HERE", "HERE1")
            }
            if (weight.isEmpty()) {
                _typologyError.value = "Please insert the weight"
                Log.d("HERE", "HERE2")
            }
            if (companyId.isEmpty()) {
                _typologyError.value = "Please insert the company ID"
                Log.d("HERE", "HERE3")
            }
            if (typology.isEmpty()) {
                _typologyError.value = "Please insert the typology"
                Log.d("HERE", "HERE4")
            }
            result = false
        }

        return result
    }

}

