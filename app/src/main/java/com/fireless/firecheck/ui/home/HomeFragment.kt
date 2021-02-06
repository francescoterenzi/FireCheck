package com.fireless.firecheck.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.databinding.FragmentHomeBinding
import com.fireless.firecheck.network.FirebaseDBMng
import com.fireless.firecheck.util.ApiStatus
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class HomeFragment : Fragment(), MaintenanceAdapter.MaintenanceAdapterListener {

    private val activityScopeJob = SupervisorJob()
    private lateinit var maintenanceAdapter: MaintenanceAdapter
    private lateinit var binding: FragmentHomeBinding
    private val activityScope = CoroutineScope(
        activityScopeJob + Dispatchers.Main)

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        maintenanceAdapter = MaintenanceAdapter(this)

        binding.list.adapter = maintenanceAdapter
        binding.viewModel = viewModel

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            if (status == ApiStatus.ERROR) onNetworkError()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.run {
            logout.setOnClickListener {
                activityScope.launch {

                    FirebaseAuth.getInstance().signOut()
                    FirebaseDBMng.resetUserInfo()
                }
            }
        }


    }

    override fun onMaintenanceClicked(cardView: View, maintenance: Maintenance) {
        // Set exit and reenter transitions here as opposed to in onCreate because these transitions
        // will be set and overwritten on HomeFragment for other navigation actions.
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        val emailCardDetailTransitionName = getString(R.string.maintenance_card_transition_name)
        val extras = FragmentNavigatorExtras(cardView to emailCardDetailTransitionName)
        val directions =
            HomeFragmentDirections.actionHomeFragmentToMaintenanceFragment(maintenance.id)
        findNavController().navigate(directions, extras)
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

}
