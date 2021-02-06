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

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    var extinguisherId: String = ""

    private fun getExtinguisherId(matricola: String): Int {
        return 1
    }

    fun createMaintenance(view: View) {
        coroutineScope.launch {
            val setPropertiesDeferred = MaintenanceApi
                .retrofitServiceSetMaintenance
                .setMaintenance(
                    extinguisherId,
                    auth.currentUser!!.uid
                )
            try {
                _status.value = ApiStatus.LOADING
                val result = setPropertiesDeferred.await()
                _status.value = ApiStatus.DONE

                view.findNavController().navigate(R.id.action_newMaintenanceFragment_to_homeFragment)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
            }
        }
    }

}