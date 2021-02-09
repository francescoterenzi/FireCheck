package com.fireless.firecheck.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.facebook.*
import com.facebook.login.LoginResult
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentLoginBinding
import com.fireless.firecheck.network.FirebaseDBMng
import com.google.firebase.auth.FacebookAuthProvider

private const val TAG = "LOGIN FRAGMENT"

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    //Facebook Callback manager
    private var callbackManager: CallbackManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        callbackManager = CallbackManager.Factory.create()

        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        // specify the fragment view as the lifecycle owner of the binding
        //this is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            binding.fb.setOnClickListener {
                binding.facebookLoginButton.performClick()
            }
            binding.facebookLoginButton.setOnClickListener {
                performFacebookLogin(view)
            }
        }
    }

    private fun performFacebookLogin(view: View) {
        binding.facebookLoginButton.setReadPermissions("email")
        binding.facebookLoginButton.setReadPermissions("public_profile")
        binding.facebookLoginButton.fragment = this

        // Callback registration
        binding.facebookLoginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                firebaseAuthWithFacebook(view, loginResult.accessToken)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                Log.d(TAG, "signInWithCredential:success $exception")
            }
        })
    }

    private fun firebaseAuthWithFacebook(view: View, token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)

        Log.d("LOGIN", credential.toString())

        FirebaseDBMng.auth.signInWithCredential(credential).addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            // Sign in success, update UI with the signed-in user's information
            Log.e(TAG, "signInWithCredential:success" + it.result)
            val currentUser = FirebaseDBMng.auth.currentUser

            val firstName = currentUser?.displayName.toString().split(" ")[0]
            val lastName = currentUser?.displayName.toString().split(" ")[1]

            FirebaseDBMng.saveUserOnFirebaseDatabase(view, firstName, lastName)
            view.findNavController().navigate((R.id.action_loginFragment_to_mainActivity))

        }.addOnFailureListener {
            Log.e(TAG, "Facebook login in Failed: ${it.message}")
            Toast.makeText(this.context, "Facebook login Failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }

}
