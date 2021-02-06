package com.fireless.firecheck.ui.login

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.fireless.firecheck.R

private const val TAG = "LOGIN VIEW MODEL"

class LoginViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    var email: String = ""
    var password: String = ""

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String>
        get() = _passwordError

    init {
        _emailError.value = ""
        _passwordError.value = ""
    }

    @SuppressLint("LogNotTimber")
    fun performLogin(view: View) {

        if (!isFormDataValid())
            return

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            Log.e(TAG, "Successfully logged in: ${it.result?.user?.uid}")
            Toast.makeText(view.context, "Successfully logged in", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate((R.id.action_loginFragment_to_mainActivity))
        }.addOnFailureListener {
            Log.e(TAG, "Log in Failed: ${it.message}")
            Toast.makeText(view.context, "Log in Failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun isFormDataValid(): Boolean {
        var result = true

        _emailError.value = null
        _passwordError.value = null

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                _emailError.value = "Please insert a valid email"
            }
            if (password.isEmpty()) {
                _passwordError.value = "Please insert the password"
            }
            result = false
        }

        return result
    }

    fun goToRegister(view: View) {
        view.findNavController().navigate((R.id.action_loginFragment_to_registrationFragment))
    }

}

