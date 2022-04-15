package com.example.parkinsonarplus.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonarplus.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var sensorManager: SensorManager

    private var mAccel: Sensor? = null
    private var mGyro: Sensor? = null
    private var mGravity: Sensor? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        binding.buttonTremor.setOnClickListener {
            if (mAccel != null) {
                println("Success! There's an accelerometer.")
            } else {
                println("Failure. No accelerometer.")
            }
            if (mGyro != null) {
                println("Success! There's a gyroscope.")
            } else {
                println("Failure. No gyroscope.")
            }
            if (mGravity != null) {
                println("Success! There's a gravity sensor.")
            } else {
                println("Failure. No gravity sensor.")
            }
        }

        return root
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // dom sumting if sensor accuracy changes
        println("sensor accuracy changed!")
    }

    override fun onSensorChanged(event: SensorEvent) {
        // most sensors return 3 values, one for each axis
        val mov0 = event.values[0]
        val mov1 = event.values[1]
        val mov2 = event.values[2]

        println("event.values[0]: $mov0")
        println("event.values[1]: $mov1")
        println("event.values[2]: $mov2")
    }

    override fun onResume() {
        super.onResume()
        mGyro?.also { gyroscope ->
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}