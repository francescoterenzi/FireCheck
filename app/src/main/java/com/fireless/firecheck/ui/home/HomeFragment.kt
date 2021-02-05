package com.fireless.firecheck.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.data.Maintenance
import com.fireless.firecheck.data.User
import com.fireless.firecheck.databinding.FragmentHomeBinding
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough


/**
 * A fragment representing a list of Items.
 */
class HomeFragment : Fragment(), MaintenanceAdapter.MaintenanceAdapterListener {

    private lateinit var binding: FragmentHomeBinding

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Postpone enter transitions to allow shared element transitions to run.
        // https://github.com/googlesamples/android-architecture-components/issues/495
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        // Only enable the on back callback if this home fragment is a mailbox other than Inbox.
        // This is to make sure we always navigate back to Inbox before exiting the app.

        val maintenanceAdapter = MaintenanceAdapter(this)

        binding.list.adapter = maintenanceAdapter

        val mList:MutableList<Maintenance> = mutableListOf<Maintenance>()

        val valore=20
        for (i in 1..valore)
        {
            val m = Maintenance(i, User(""))
            mList.add(m)
        }

        maintenanceAdapter.submitList(mList)


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

}
