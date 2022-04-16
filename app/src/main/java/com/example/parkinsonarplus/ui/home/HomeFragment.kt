package com.example.parkinsonarplus.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonarplus.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), SensorEventListener, OnTouchListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var sensorManager: SensorManager

    private var mAccel: Sensor? = null
    private var mGyro: Sensor? = null
    private var mGravity: Sensor? = null

    private var buttonClick: Boolean = false
    private var buttonDown: Boolean = false

    private var gyroX: Float = 0F // rate of rotation around x-axis (rad/s)
    private var gyroY: Float = 0F
    private var gyroZ: Float = 0F

    var sensational: Intent? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
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

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)


        binding.buttonTremor.setOnTouchListener(this)

        return root
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // dom sumting if sensor accuracy changes
        println("sensor accuracy changed!")
    }

    override fun onSensorChanged(event: SensorEvent) {
        // most sensors return 3 values, one for each axis
        gyroX = event.values[0]
        gyroY = event.values[1]
        gyroZ = event.values[2]

//        println("event.values[0]: $mov0")
//        println("event.values[1]: $mov1")
//        println("event.values[2]: $mov2")
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (event != null) {

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    sensational =  Intent(this.requireContext(), Sensational::class.java).also { intent ->
                        requireContext().startService(intent)
                    }

                    println("action_down")

                    // log/record sensor inputs

                    if (gyroX < -1 || gyroX > 1) {

                        println("gyroX: $gyroX")
                    }
                    if (gyroY < -1 || gyroY > 1) {

                        println("gyroY: $gyroY")
                    }
                    if (gyroZ < -1 || gyroZ > 1) {

                        println("gyroZ $gyroZ")
                    }

                    buttonDown = true

                    return true
                }

                MotionEvent.ACTION_UP -> {
                    println("action_up")

                    this.requireContext().stopService(sensational)

                    buttonDown = false

                    return true
                }
            }
        }
        return false
    }
}