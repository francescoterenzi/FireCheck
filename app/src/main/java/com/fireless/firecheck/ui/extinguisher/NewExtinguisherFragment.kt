package com.fireless.firecheck.ui.extinguisher

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.transition.Slide
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentNewExtinguisherBinding
import com.fireless.firecheck.network.ExtinguisherApi
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class NewExtinguisherFragment : Fragment() {

    private var _extinguisherId = ""
    private var _companyId = ""
    private var _weight = ""
    private var _typology = ""

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    /*
    private val viewModel: NewExtinguisherViewModel by lazy {
        ViewModelProvider(this).get(NewExtinguisherViewModel::class.java)
    }
     */

    private lateinit var binding: FragmentNewExtinguisherBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewExtinguisherBinding.inflate(inflater, container, false)
        //binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.typology,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.typology.adapter = adapter
        }


        binding.run {

            // Set transitions here so we are able to access Fragment's binding views.
            enterTransition = MaterialContainerTransform().apply {
                // Manually add the Views to be shared since this is not a standard Fragment to
                // Fragment shared element transition.
                startView = requireActivity().findViewById(R.id.fab)
            }
            returnTransition = Slide().apply {
                duration = resources.getInteger(R.integer.motion_duration_medium).toLong()
            }

            typology.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View,
                                            position: Int, id: Long) {

                    val item = adapterView.getItemAtPosition(position)
                    _typology = item.toString()

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

            newExtinguisherBtn.setOnClickListener {
                _extinguisherId = extinguisherIdEditText.text.toString()
                _weight = weightEditText.text.toString()
                _companyId = companyIdEditText.text.toString()
                createExtinguisher(view)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard(requireActivity())

    }

    private fun hideKeyboard(activity: Activity) {
        val inputManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // check if no view has focus:
        val currentFocusedView = activity.currentFocus
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(
                    currentFocusedView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


    private fun createExtinguisher(view: View) {

        coroutineScope.launch {
            val setPropertiesDeferred = ExtinguisherApi
                .retrofitServiceSetExtinguisher
                .setExtinguisher(_extinguisherId, _weight, _typology, _companyId)
            try {
                setPropertiesDeferred.await()
                view.findNavController()
                    .navigate(R.id.action_newExtinguisherFragment_to_homeFragment)

            } catch (e: Exception) {
                Log.e("NEW EXT FRAG", "$e")
            }
        }
    }
}