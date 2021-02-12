package com.fireless.firecheck.ui.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fireless.firecheck.R
import com.fireless.firecheck.databinding.FragmentStatisticsBinding
import com.fireless.firecheck.network.UserApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class StatisticsFragment : Fragment() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main
    )

    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        val xvalues = FloatArray(13)
        val yvalues = FloatArray(13)


        //setGraphData(FirebaseAuth.getInstance().currentUser!!.uid)


        for (i in 0..12) {
            xvalues[i] = (i).toFloat()
            yvalues[i] = ((Random().nextInt(4) + 1).toFloat())
        }

        graph.setData(xvalues, yvalues)

    }


    private fun setGraphData(userId: String) {

        coroutineScope.launch {

            val map = mutableMapOf<Float, Float>()
            for (i in 0..19) {
                map[i.toFloat()] = 0F
            }

            val getPropertiesDeferred = UserApi
                .retrofitServiceGetUserControls
                .getUserControls(userId)

            try {
                val res = getPropertiesDeferred.await()

                for(elem in res) {
                    val month = elem.dateOfControl.split("/")[1].toFloat()
                    map[month]?.plus(1)?.let { map.put(month, it) }
                }

                val xvalues = FloatArray(map.size)
                val yvalues = FloatArray(map.size)

                for (i in 0..map.size) {
                    xvalues[i] = map.keys.elementAt(i)
                    yvalues[i] = map[i]!!
                }

                //Log.d("AOOOOO", ""+ xvalues[1])

                //binding.graph.setData(xvalues, yvalues)

            } catch (e: Exception) {
                Log.d("STATISTICS", e.toString())
            }
        }
    }

}