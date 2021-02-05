package com.fireless.firecheck.ui.login

import android.annotation.SuppressLint
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentLoginBinding
import com.fireless.firecheck.network.FirebaseDBMng
import com.google.android.gms.auth.api.signin.GoogleSignIn

private const val TAG = "LOGIN FRAGMENT"

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    private val RC_SIGN_IN: Int = 1001

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        // specify the fragment view as the lifecycle owner of the binding
        //this is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("LogNotTimber")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed $e")
                Toast.makeText(this.context, "Google sign in failed ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseDBMng.auth.signInWithCredential(credential).addOnCompleteListener() {
            if (!it.isSuccessful) return@addOnCompleteListener
            Log.e(TAG, "signInWithCredential:success" + it.result)
            FirebaseDBMng.saveUserOnFirebaseDatabase(view)

        }.addOnFailureListener {
            Log.e(TAG, "Google log in  Failed: ${it.message}")
            Toast.makeText(this.context, "Google login Failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

}
