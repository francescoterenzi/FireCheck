package com.fireless.firecheck.ui.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentHomeBinding
import com.fireless.firecheck.databinding.FragmentStatisticsBinding
import com.fireless.firecheck.models.Maintenance
import com.fireless.firecheck.network.FirebaseDBMng
import com.fireless.firecheck.network.UserApi
import com.fireless.firecheck.ui.home.MaintenanceAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.coroutines.*
import java.util.*


class StatisticsFragment : Fragment() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

    private lateinit var binding: FragmentStatisticsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistics,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.closeIcon.setOnClickListener {
            findNavController().navigateUp()
        }
        getControls(FirebaseAuth.getInstance().currentUser!!.uid)
    }

    private fun generateRandomDataPoints(): List<DataPoint> {
        val random = Random()
        return (0..19).map {
            DataPoint(it, random.nextInt(10) + 1)
        }
    }

    private fun getControls(userId: String) {

        coroutineScope.launch {

            val map = mutableMapOf<Int, Int>()
            for (i in 0..19) {
                map[i] = 0
            }

            val getPropertiesDeferred = UserApi
                .retrofitServiceGetUserControls
                .getUserControls(userId)

            try {
                val res = getPropertiesDeferred.await()

                for(elem in res) {
                    val month = elem.dateOfControl.split("/")[1].toInt()
                    Log.d("XK?", month.toString())
                    map[month]?.plus(1)?.let { map.put(month, it) }
                 }

                for (elem in map) {
                    Log.d("AOODOOOODODODODO", elem.key.toString() + " " + elem.value.toString())
                }

                graph_view.setData(map.map {
                    DataPoint(it.key, it.value)
                })

            } catch (e: Exception) {
                Log.d("STATISTICS", e.toString())
            }
        }
    }
}