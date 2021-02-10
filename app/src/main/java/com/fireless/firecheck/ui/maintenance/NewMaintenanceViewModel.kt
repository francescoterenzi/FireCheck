package com.fireless.firecheck.ui.maintenance

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.network.MaintenanceApi
import com.fireless.firecheck.util.ApiStatus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "NEW MAINT VIEW MODEL"

class NewMaintenanceViewModel : ViewModel() {

    var extinguisherId: String = ""
    var date: MutableLiveData<String> = MutableLiveData()

    private val _extinguisherIdError = MutableLiveData<String>()
    val extinguisherIdError: LiveData<String>
        get() = _extinguisherIdError

    private val _dateError = MutableLiveData<String>()
    val dateError: LiveData<String>
        get() = _dateError

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main
    )

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        _extinguisherIdError.value = ""
        _dateError.value = ""
    }


    fun createMaintenance(view: View) {

        if (!isFormDataValid())
            return

        coroutineScope.launch {
            val setPropertiesDeferred = MaintenanceApi
                .retrofitServiceSetMaintenance
                .setMaintenance(
                        date.value!!.toString(),
                        extinguisherId,
                        auth.currentUser!!.uid)
            try {
                _status.value = ApiStatus.LOADING
                setPropertiesDeferred.await()
                _status.value = ApiStatus.DONE

                view.findNavController().navigate(R.id.action_newMaintenanceFragment_to_homeFragment)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
            }
        }
    }

    private fun isFormDataValid(): Boolean {
        var validForm = true

        _extinguisherIdError.value = null
        _dateError.value = null

        if (extinguisherId.isEmpty() || date.value!!.isEmpty()) {
            if (extinguisherId.isEmpty())
                _extinguisherIdError.value = "Please insert a valid extinguisher ID"
            if (date.value!!.isEmpty())
                _dateError.value = "Please insert the maintenance date"
            validForm = false

        }

        return validForm
    }

    fun setDate(date_of_maintenance: String) {
        date.value = date_of_maintenance
    }


}