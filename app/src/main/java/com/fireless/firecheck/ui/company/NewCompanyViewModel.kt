package com.fireless.firecheck.ui.company

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.network.CompanyApi
import com.fireless.firecheck.util.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "NEW COMP VIEW MODEL"

class NewCompanyViewModel : ViewModel() {


    private val _companyIdError = MutableLiveData<String>()
    val companyIdError: LiveData<String>
        get() = _companyIdError

    private val _nameError = MutableLiveData<String>()
    val nameError: LiveData<String>
        get() = _nameError

    private val _addressError = MutableLiveData<String>()
    val addressError: LiveData<String>
        get() = _addressError

    init {
        _companyIdError.value = ""
        _nameError.value = ""
        _addressError.value = ""
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    val companyId = ""
    val name = ""
    val address = ""

    fun createCompany(view: View) {
        if (!isFormDataValid())
            return

        coroutineScope.launch {
            val setPropertiesDeferred = CompanyApi
                    .retrofitServiceSetCompany
                    .setCompany(companyId, name, address)
            try {
                setPropertiesDeferred.await()
                view.findNavController().navigate(R.id.action_newCompanyFragment_to_homeFragment)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }

    private fun isFormDataValid(): Boolean {
        var result = true

        _companyIdError.value = null
        _nameError.value = null
        _addressError.value = null

        if (companyId.isEmpty() ||
                name.isEmpty() ||
                address.isEmpty()) {

            if (companyId.isEmpty()) {
                _companyIdError.value = "Please insert a valid company ID"
            }
            if (name.isEmpty()) {
                _nameError.value = "Please insert the name"
            }
            if (address.isEmpty()) {
                _addressError.value = "Please insert the company address"
            }

            result = false
        }

        return result
    }

}

