package com.fireless.firecheck.ui.maintenance

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentMaintenanceBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.transition.MaterialContainerTransform


class MaintenanceFragment : Fragment() {

    private val params by navArgs<MaintenanceFragmentArgs>()

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private lateinit var binding: FragmentMaintenanceBinding

    private val viewModel: MaintenanceViewModel by lazy {
        ViewModelProvider(this).get(MaintenanceViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            //setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMaintenanceBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loadInfo(params.maintenanceId)

        /**
         * MAPS INIT
         */
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync { mMap ->
            googleMap = mMap

            // For dropping a marker at a point on the Map
            val sydney = LatLng(-34.00, 151.00)
            googleMap.addMarker(
                MarkerOptions().position(sydney)
            )

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}