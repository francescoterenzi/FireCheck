package com.fireless.firecheck.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.models.User
import com.fireless.firecheck.network.FirebaseDBMng
import com.fireless.firecheck.network.UserApi
import com.fireless.firecheck.util.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "HOME VIEW MODEL"

class HomeViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status


    private val _allControls = MutableLiveData<List<Maintenance>>()
    val allControls: LiveData<List<Maintenance>>
        get() = _allControls

    init {
        getControls()
    }

    private fun getControls() {
        coroutineScope.launch {
            /*
            val getPropertiesDeferred = UserApi
                .retrofitServiceGetUserControls
                .getUserControls(FirebaseDBMng.auth.currentUser?.uid.toString())
            */

            val mList:MutableList<Maintenance> = mutableListOf<Maintenance>()

            val valore=20
            for (i in 1..valore)
            {
                val m = Maintenance(i, User(""))
                mList.add(m)
            }

            try {

                _status.value = ApiStatus.LOADING
                //val listResult = getPropertiesDeferred.await()
                val listResult = mList
                _allControls.value = listResult
                _status.value = ApiStatus.DONE

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _allControls.value = ArrayList()
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
