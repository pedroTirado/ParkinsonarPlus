package com.example.parkinsonarplus.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonarplus.databinding.FragmentHomeBinding
import java.util.*


class HomeFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var sensorManager: SensorManager

    private var mGyro: Sensor? = null // gyroscope (raw input; hardware-based)
    private var mAccel: Sensor? = null // accelerometer (raw input; hardware-based)
    private var mGravity: Sensor? = null // gravity sensor (non-raw input; software-based)
    private var mLinaccel: Sensor? = null // linear accelerometer (non-raw input; software-based)
    private var mRotvec: Sensor? = null // rotation vector sensor (non-raw input; software-based)

    private var gyroX: Float = 0F // rate of rotation around x-axis (rad/s)
    private var gyroY: Float = 0F
    private var gyroZ: Float = 0F

    private var accelX: Float = 0F // acceleration applied to device, including gravity (m/s^2)
    private var accelY: Float = 0F
    private var accelZ: Float = 0F

    private var gravX: Float = 0F // x-component gravitational force (m/s^2)
    private var gravY: Float = 0F
    private var gravZ: Float = 0F

    private var laccelX: Float = 0F // acceleration applied to device, excluding gravity (m/s^2)
    private var laccelY: Float = 0F
    private var laccelZ: Float = 0F

    private var rotvecX: Float = 0F // device orientation (unitless)
    private var rotvecY: Float = 0F
    private var rotvecZ: Float = 0F

    private var gravXAvg: Float = 0F
    private var gravYAvg: Float = 0F
    private var gravZAvg: Float = 0F

    private var gravXList: ArrayList<Float>? = null
    private var gravYList: ArrayList<Float>? = null
    private var gravZList: ArrayList<Float>? = null

    private val viewModel: HomeViewModel by activityViewModels() // for inter-fragment communication

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
        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        gravXList = ArrayList<Float>(20)
        gravYList = ArrayList<Float>(20)
        gravZList = ArrayList<Float>(20)

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // log/record sensor inputs

//        viewModel.gyroX.value = this.gyroX // update gyroX
//        println("HomeViewModel.gyroX: ${viewModel.gyroX.value}")
//        viewModel.gyroY.value = this.gyroY // update gyroY
//        println("HomeViewModel.gyroY: ${viewModel.gyroY.value}")
//        viewModel.gyroZ.value = this.gyroZ // update gyroZ
//        println("HomeViewModel.gyroZ: ${viewModel.gyroZ.value}")
//
//        if (gyroX < -1 || gyroX > 1) {
//            println("gyroX: $gyroX")
//        }
//        if (gyroY < -1 || gyroY > 1) {
//            println("gyroY: $gyroY")
//        }
//        if (gyroZ < -1 || gyroZ > 1) {
//            println("gyroZ $gyroZ")
//        }

        binding.buttonTremor.setOnTouchListener(object : OnTouchListener {

            private var mHandler: Handler? = null

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        if (mHandler != null)
                            return true
                        else {
                            mHandler = Handler(Looper.getMainLooper())
                            mHandler!!.postDelayed(mAction, 0)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (mHandler == null)
                            return true
                        else {
                            mHandler!!.removeCallbacks(mAction)
                            mHandler = null

                            println("gravXList: ${gravXList.toString()}")
                            println("gravYList: ${gravYList.toString()}")
                            println("gravZList: ${gravZList.toString()}")

                            // average the sensor values
                            gravXAvg = takeAvg(gravXList)
                            gravYAvg = takeAvg(gravYList)
                            gravZAvg = takeAvg(gravZList)

                            println("gravXAvg: $gravXAvg")
                            println("gravYAvg: $gravYAvg")
                            println("gravZAvg: $gravZAvg")

                            // clear the arraylists (to conserve memory & start fresh for next button press)
                            gravXList?.clear()
                            gravYList?.clear()
                            gravZList?.clear()

                            // call method(s) to evaluate resting tremor given averaged sensor readings
                        }
                    }
                }
                return false
            }

            var mAction: Runnable = object : Runnable {
                override fun run() {
//                    println("gyroX: $gyroX")
//                    println("gyroY: $gyroY")
//                    println("gyroZ: $gyroZ")
//                    println("gravX: $gravX")
//                    println("gravY: $gravY")
//                    println("gravZ: $gravZ")
//                    println("====================================================================")

                    gravXList?.add(gravX)
                    gravYList?.add(gravY)
                    gravZList?.add(gravZ)

                    mHandler?.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun takeAvg(sensorList: ArrayList<Float>?): Float { // takes the avg. of param. list

        var runSum: Float = 0.0F
        var avg: Float = 0.0F

        if (sensorList != null) {
            for (f in sensorList) {
                runSum += f
            }
            avg = runSum / sensorList.size
        }
        return avg
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // dom sumting if sensor accuracy changes
        println("sensor accuracy changed!")
    }

    override fun onSensorChanged(event: SensorEvent) {
        // most sensors return 3 values, one for each axis
//        gyroX = event.values[0] // TODO: convert each of these to array lists...
//        gyroY = event.values[1]
//        gyroZ = event.values[2]

        gravX = event.values[0] // TODO: convert each of these to array lists...
        gravY = event.values[1]
        gravZ = event.values[2]

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
}