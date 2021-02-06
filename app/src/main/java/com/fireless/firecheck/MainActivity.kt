package com.fireless.firecheck

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.fireless.firecheck.databinding.ActivityMainBinding
import com.fireless.firecheck.network.FirebaseDBMng
import com.fireless.firecheck.network.FirebaseUserLiveData
import com.fireless.firecheck.ui.extinguisher.NewExtinguisherFragmentDirections
import com.fireless.firecheck.ui.login.LoginActivity
import com.fireless.firecheck.ui.maintenance.DatePickerFragment
import com.fireless.firecheck.ui.maintenance.NewMaintenanceFragmentDirections
import com.google.android.material.transition.MaterialElevationScale
import com.materialstudies.reply.util.contentView


class MainActivity : AppCompatActivity(),
        Toolbar.OnMenuItemClickListener,
        NavController.OnDestinationChangedListener {


    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //observe if a user is logged or not
        FirebaseUserLiveData().observe(this, { authenticationState ->
            if (authenticationState == null) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // initialization of user info (Firebase realtime database)
                FirebaseDBMng.initFirebaseDB()
                setUpBottomNavigationAndFab()
            }
        })
    }

    private fun setUpBottomNavigationAndFab() {
        // Wrap binding.run to ensure ContentViewBindingDelegate is calling this Activity's
        // setContentView before accessing views
        binding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
        }

        // Set a custom animation for showing and hiding the FAB
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                navigateToNewMaintenance()
            }
        }

        // Set up the BottomAppBar menu
        binding.bottomAppBar.apply {
            setOnMenuItemClickListener(this@MainActivity)
        }

    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // Set the currentEmail being viewed so when the FAB is pressed, the correct email
        // reply is created. In a real app, this should be done in a ViewModel but is done
        // here to keep things simple. Here we're also setting the configuration of the
        // BottomAppBar and FAB based on the current destination.
        when (destination.id) {
            R.id.homeFragment -> {
                setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            }
            R.id.newMaintenanceFragment -> {
                setBottomAppBarForNewMaintenance()
            }
            R.id.maintenanceFragment -> {
                setBottomAppBarForNewMaintenance()
            }
            R.id.newExtinguisherFragment -> {
                setBottomAppBarForNewExtinguisher()
            }
        }
    }


    @MenuRes
    private fun getBottomAppBarMenuForDestination(destination: NavDestination? = null): Int {
        val dest = destination ?: findNavController(R.id.nav_host_fragment).currentDestination
        return when (dest?.id) {
            R.id.homeFragment -> R.menu.bottom_app_bar_home_menu
            else -> R.menu.bottom_app_bar_home_menu
        }
    }

    private fun setBottomAppBarForHome(@MenuRes menuRes: Int) {
        binding.run {
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(menuRes)
            //fab.contentDescription = getString(0)
            bottomAppBar.performShow()
            fab.show()
        }
    }

    private fun setBottomAppBarForNewMaintenance() {
        hideBottomAppBar()
    }

    private fun setBottomAppBarForNewExtinguisher() {
        hideBottomAppBar()
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            // Get a handle on the animator that hides the bottom app bar so we can wait to hide
            // the fab and bottom app bar until after it's exit animation finishes.
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    // Hide the BottomAppBar to avoid it showing above the keyboard
                    // when composing a new email.
                    bottomAppBar.visibility = View.GONE
                    fab.visibility = View.INVISIBLE
                }
                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    private fun navigateToNewMaintenance() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
        }

        val directions = NewMaintenanceFragmentDirections.actionGlobalNewMaintenanceFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToNewExtinguisher() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
        }

        val directions = NewExtinguisherFragmentDirections.actionGlobalNewExtinguisherFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.new_extinguisher -> {
                navigateToNewExtinguisher()
            }
        }
        return true
    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

}