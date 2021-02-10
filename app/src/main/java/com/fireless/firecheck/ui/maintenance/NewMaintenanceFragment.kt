package com.fireless.firecheck.ui.maintenance

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentNewMaintenanceBinding
import com.google.android.material.transition.MaterialContainerTransform

class NewMaintenanceFragment : Fragment() {

    private lateinit var binding: FragmentNewMaintenanceBinding
    private lateinit var viewModel: NewMaintenanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewMaintenanceBinding.inflate(inflater, container, false)

        viewModel =
                ViewModelProvider(requireActivity()).get(NewMaintenanceViewModel::class.java)

        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val dateObserver = Observer<String> { newDate ->
            binding.dateEditText.setText(newDate)
        }
        viewModel.date.observe(requireActivity(), dateObserver)

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

            closeIcon.setOnClickListener {
                findNavController().navigateUp()
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

}