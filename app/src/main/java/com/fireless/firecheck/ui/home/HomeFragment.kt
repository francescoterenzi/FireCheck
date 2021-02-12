package com.fireless.firecheck.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentHomeBinding
import com.fireless.firecheck.models.MaintenanceProperty
import com.fireless.firecheck.network.UserApi
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

/**
 * A fragment representing a list of Items.
 */
class HomeFragment : Fragment(), MaintenanceAdapter.MaintenanceAdapterListener {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val maintenanceAdapter = MaintenanceAdapter(this)
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
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.apply {
            val itemTouchHelper = ItemTouchHelper(ReboundingSwipeActionCallback())
            itemTouchHelper.attachToRecyclerView(this)
            adapter = maintenanceAdapter
        }
        binding.list.adapter = maintenanceAdapter

        getControls(FirebaseAuth.getInstance().currentUser!!.uid).observe(viewLifecycleOwner) {
            maintenanceAdapter.submitList(it)
        }

        getName(FirebaseAuth.getInstance().currentUser!!.uid)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }

    override fun onMaintenanceClicked(cardView: View, maintenance: MaintenanceProperty) {
        // Set exit and reenter transitions here as opposed to in onCreate because these transitions
        // will be set and overwritten on HomeFragment for other navigation actions.
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }

        val directions =
            HomeFragmentDirections.actionHomeFragmentToMaintenanceFragment(maintenance.id)
        findNavController().navigate(directions)
    }

    private fun getControls(userId: String): LiveData<List<MaintenanceProperty>> {

        val controlList = MutableLiveData<List<MaintenanceProperty>>()

        coroutineScope.launch {
            val getPropertiesDeferred = UserApi
                    .retrofitServiceGetUserControls
                    .getUserControls(userId)

            try {
                val res = getPropertiesDeferred.await()
                val list = arrayListOf<MaintenanceProperty>()
                 for(elem in res.reversed()) {
                     val m = MaintenanceProperty(
                             elem.id.toString(),
                             elem.dateOfControl,
                             elem.userId,
                             elem.extinguisherId
                     )
                     list.add(m)
                 }
                controlList.value = list

            } catch (e: Exception) {
                Log.e("HOME FRAG", "$e")
                controlList.value = ArrayList()
            }
        }
        return controlList
    }

    @SuppressLint("SetTextI18n")
    private fun getName(id: String) {


        coroutineScope.launch {

            val getPropertiesDeferred = UserApi
                .retrofitServiceGetUser
                .getUser(id)

            try {
                val user = getPropertiesDeferred.await()
                binding.technicianName.text = user.firstName + " " + user.lastName

            } catch (e: Exception) {
                Log.d("STATISTICS", e.toString())
            }
        }
    }

}
