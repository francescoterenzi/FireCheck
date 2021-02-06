package com.fireless.firecheck.ui.maintenance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentNewMaintenanceBinding
import com.google.android.material.transition.MaterialContainerTransform

class NewMaintenanceFragment : Fragment() {

    private lateinit var binding: FragmentNewMaintenanceBinding

    private val viewModel: NewMaintenanceViewModel by lazy {
        ViewModelProvider(this).get(NewMaintenanceViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewMaintenanceBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
        }
    }

}