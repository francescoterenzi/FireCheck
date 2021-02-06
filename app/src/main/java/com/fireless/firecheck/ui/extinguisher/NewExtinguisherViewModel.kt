package com.fireless.firecheck.ui.extinguisher

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.network.ExtinguisherApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "NEW EXT VIEW MODEL"

class NewExtinguisherViewModel : ViewModel() {


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


    val extinguisherId = ""
    val weight = ""
    val typology = ""
    val companyId = ""

    fun createExtinguisher(view: View) {
        if (!isFormDataValid())
            return

        coroutineScope.launch {
            val setPropertiesDeferred = ExtinguisherApi
                .retrofitServiceSetExtinguisher
                .setExtinguisher(extinguisherId, weight, typology, companyId)
            try {
                setPropertiesDeferred.await()
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
            }
            if (weight.isEmpty()) {
                _typologyError.value = "Please insert the weight"
            }
            if (companyId.isEmpty()) {
                _typologyError.value = "Please insert the company name"
            }
            if (typology.isEmpty()) {
                _typologyError.value = "Please insert the typology"
            }
            result = false
        }

        return result
    }

}

