package com.fireless.firecheck.ui.company

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.fireless.firecheck.BuildConfig
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentNewCompanyBinding
import com.fireless.firecheck.ui.extinguisher.Constants
import com.fireless.firecheck.util.FetchAddressIntentService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialContainerTransform

@Suppress("DEPRECATION")
class NewCompanyFragment : Fragment() {

    private val TAG = NewCompanyFragment::class.java.simpleName

    private val viewModel: NewCompanyViewModel by lazy {
        ViewModelProvider(this).get(NewCompanyViewModel::class.java)
    }

    private lateinit var binding: FragmentNewCompanyBinding

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val ADDRESS_REQUESTED_KEY = "address-request-pending"
    private val LOCATION_ADDRESS_KEY = "location-address"

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var fusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Represents a geographical location.
     */
    private var lastLocation: Location? = null

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private var addressRequested = false

    /**
     * The formatted location address.
     */
    private var addressOutput = ""

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private lateinit var resultReceiver: AddressResultReceiver

    /**
     * Displays the location address.
     */
    private lateinit var locationAddressTextView: TextInputEditText

    /**
     * Visible while the address is being fetched.
     */
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewCompanyBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel

        resultReceiver = AddressResultReceiver(Handler())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationAddressTextView = binding.addressEditText
        progressBar = binding.progressBar

        // Set defaults, then update using values stored in the Bundle.
        addressRequested = false
        addressOutput = ""
        updateValuesFromBundle(savedInstanceState)

        updateUIWidgets()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.run {

            address.setEndIconOnClickListener {
                // Respond to end icon presses
                fetchAddressButtonHandler()
            }

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

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        ADDRESS_REQUESTED_KEY.let {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(it)) {
                addressRequested = savedInstanceState.getBoolean(it)
            }
        }

        LOCATION_ADDRESS_KEY.let {
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(it)) {
                addressOutput = savedInstanceState.getString(it).toString()
                displayAddressOutput()
            }
        }


    }

    /**
     * Runs when user clicks the Fetch Address button.
     */
    fun fetchAddressButtonHandler() {
        if (lastLocation != null) {
            startIntentService()
            return
        }

        // If we have not yet retrieved the user location, we process the user's request by setting
        // addressRequested to true. As far as the user is concerned, pressing the Fetch Address
        // button immediately kicks off the process of getting the address.
        addressRequested = true
        updateUIWidgets()
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private fun startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent = Intent(requireActivity(), FetchAddressIntentService::class.java).apply {
            // Pass the result receiver as an extra to the service.
            putExtra(Constants.RECEIVER, resultReceiver)

            // Pass the location data as an extra to the service.
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        requireActivity().startService(intent)
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    private fun getAddress() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(
                requireActivity(),
                OnSuccessListener { location ->
                    if (location == null) {
                        Log.w(TAG, "onSuccess:null")
                        return@OnSuccessListener
                    }

                    lastLocation = location

                    // Determine whether a Geocoder is available.
                    if (!Geocoder.isPresent()) {
                        this.view?.let {
                            Snackbar.make(
                                    it.findViewById<View>(android.R.id.content),
                                    R.string.no_geocoder_available, Snackbar.LENGTH_LONG
                            ).show()
                        }
                        return@OnSuccessListener
                    }

                    // If the user pressed the fetch address button before we had the location,
                    // this will be set to true indicating that we should kick off the intent
                    // service after fetching the location.
                    if (addressRequested) startIntentService()
                })!!.addOnFailureListener(requireActivity()) { e -> Log.w(
                TAG,
                "getLastLocation:onFailure",
                e
        ) }
    }

    /**
     * Updates the address in the UI.
     */
    private fun displayAddressOutput() {
        locationAddressTextView.setText(addressOutput)
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private fun updateUIWidgets() {
        if (addressRequested) {
            progressBar.visibility = ProgressBar.VISIBLE
        } else {
            progressBar.visibility = ProgressBar.GONE
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        with(savedInstanceState) {
            // Save whether the address has been requested.
            putBoolean(ADDRESS_REQUESTED_KEY, addressRequested)

            // Save the address string.
            putString(LOCATION_ADDRESS_KEY, addressOutput)
        }

        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private inner class AddressResultReceiver(
            handler: Handler
    ) : ResultReceiver(handler) {

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY).toString()
            displayAddressOutput()

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(requireActivity(), R.string.address_found, Toast.LENGTH_SHORT).show()
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            addressRequested = false
            updateUIWidgets()
        }
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
            mainTextStringId: Int, actionStringId: Int,
            listener: View.OnClickListener
    ) {
        val container: View = requireActivity().findViewById<View>(android.R.id.content)

        Snackbar.make(container, mainTextStringId.toString(), Snackbar.LENGTH_INDEFINITE).setAction(
                actionStringId.toString(), listener
        ).show()
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale, android.R.string.ok
            ) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                );
            }

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")

        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return

        when {
            grantResults.isEmpty() ->
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                getAddress()
            else -> // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

                showSnackbar(R.string.permission_denied_explanation, R.string.settings
                ) {
                    // Build intent that displays the App settings screen.
                    val intent = Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
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