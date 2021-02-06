package com.fireless.firecheck.ui.registration

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.network.FirebaseDBMng

private const val TAG = "Registration View Model"

class RegistrationViewModel : ViewModel() {

    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var username: String = ""

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _usernameError = MutableLiveData<String>()
    val usernameError: LiveData<String>
        get() = _usernameError

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String>
        get() = _passwordError

    private val _confirmPasswordError = MutableLiveData<String>()
    val confirmPasswordError: LiveData<String>
        get() = _confirmPasswordError

    init {
        _emailError.value = ""
        _passwordError.value = ""
        _confirmPasswordError.value = ""
    }

    @SuppressLint("LogNotTimber")
    fun performRegistration(view: View) {

        if (!isFormDataValid())
            return

        // Firebase registration -> create a user with email and password attributes
        FirebaseDBMng.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.e(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

                FirebaseDBMng.saveUserOnFirebaseDatabase(view)
                view.findNavController().navigate((R.id.action_registrationFragment_to_mainActivity))
            }
            .addOnFailureListener {
                Toast.makeText(view.context, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Failed to create user: ${it.message}")
            }
    }


    private fun isFormDataValid(): Boolean {
        var validForm = true

        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            if (email.isEmpty())
                _emailError.value = "Please insert a valid email"
            if (password.isEmpty())
                _passwordError.value = "Please insert the password"
            if (confirmPassword.isEmpty())
                _confirmPasswordError.value = "Please insert the confirmation password"
            validForm = false

        } else
            if (password != confirmPassword) {
                _passwordError.value = " "
                _confirmPasswordError.value = "Passwords do not match"
                validForm = false
            }

        return validForm
    }

    fun moveToLogin(view: View) {
        view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
    }
}