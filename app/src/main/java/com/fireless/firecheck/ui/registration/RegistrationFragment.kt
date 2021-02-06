package com.fireless.firecheck.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_registration,
            container,
            false)

        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)

        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        // specify the fragment view as the lifecycle owner of the binding
        //this is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}