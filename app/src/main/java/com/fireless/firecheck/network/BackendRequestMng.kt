package com.fireless.firecheck.network

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fireless.firecheck.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "BACKEND MANAGEMENT"

object FirebaseDBMng {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private val _userDB = MutableLiveData<User?>()
    val userDB: LiveData<User?>
        get() = _userDB

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var dbReference: DatabaseReference


    /**
     * FIREBASE API MANAGEMENT
     */

    // initialization of the current user's observer on the firebase real database #called on LoggedActivity#
    fun initFirebaseDB() {
        dbReference = FirebaseDatabase.getInstance().getReference("/users/${auth.currentUser?.uid}")
        dbReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.getValue(User::class.java)?.let { _userDB.value = it }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun saveUserOnFirebaseDatabase(view: View?) {
        val currentUser = auth.currentUser
        val dbReference = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}")

        dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.getValue(User::class.java).let {
                    val user = User(currentUser?.uid!!)
                    dbReference.setValue(user)
                        .addOnSuccessListener {
                            Log.d(TAG, "user saved on firebase database")
                            saveUserOnBackend(currentUser.uid)
                            Toast.makeText(view?.context, "Recorded correctly, we're almost there.", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun saveUserOnBackend(uuid: String) {
        coroutineScope.launch {
            val setUserDeferred = UserApi.retrofitServiceSetUser.setUser(uuid)
            try {
                val result = setUserDeferred.await()
                Log.e(TAG, "$result")
            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }


    fun resetUserInfo() {
        _userDB.value = null
    }
}