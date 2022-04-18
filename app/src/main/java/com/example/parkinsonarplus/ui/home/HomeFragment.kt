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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonarplus.databinding.FragmentHomeBinding
import java.util.*


class HomeFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var sensorManager: SensorManager

//    private var mGyro: Sensor? = null // gyroscope (raw input; hardware-based)
//    private var mAccel: Sensor? = null // accelerometer (raw input; hardware-based)
    private var mGravity: Sensor? = null // gravity sensor (non-raw input; software-based)
    private var mLinaccel: Sensor? = null // linear accelerometer (non-raw input; software-based)
    private var mRotvec: Sensor? = null // rotation vector sensor (non-raw input; software-based)

//    private var gyroX: Float = 0F // rate of rotation around x-axis (rad/s)
//    private var gyroY: Float = 0F
//    private var gyroZ: Float = 0F

//    private var accelX: Float = 0F // acceleration applied to device, including gravity (m/s^2)
//    private var accelY: Float = 0F
//    private var accelZ: Float = 0F

    private var gravX: Float = 0F // x-component gravitational force (m/s^2)
    private var gravY: Float = 0F
    private var gravZ: Float = 0F

    private var laccelX: Float = 0F // acceleration applied to device, excluding gravity (m/s^2)
    private var laccelY: Float = 0F
    private var laccelZ: Float = 0F

    private var rotvecX: Float = 0F // device orientation (unitless)
    private var rotvecY: Float = 0F
    private var rotvecZ: Float = 0F
    private var rotvecW: Float = 0F // magnitude of rotational vector

    // =======================================================================

    private var gravXAvg: Float = 0F
    private var gravYAvg: Float = 0F
    private var gravZAvg: Float = 0F

    private var laccelXAvg: Float = 0F
    private var laccelYAvg: Float = 0F
    private var laccelZAvg: Float = 0F

    private var rotXAvg: Float = 0F
    private var rotYAvg: Float = 0F
    private var rotZAvg: Float = 0F
    private var rotWAvg: Float = 0F

    // =======================================================================

    private var gravXList: ArrayList<Float>? = null
    private var gravYList: ArrayList<Float>? = null
    private var gravZList: ArrayList<Float>? = null

    private var laccelXList: ArrayList<Float>? = null
    private var laccelYList: ArrayList<Float>? = null
    private var laccelZList: ArrayList<Float>? = null

    private var rotXList: ArrayList<Float>? = null
    private var rotYList: ArrayList<Float>? = null
    private var rotZList: ArrayList<Float>? = null
    private var rotWList: ArrayList<Float>? = null

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

//        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        mLinaccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mRotvec = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        gravXList = ArrayList<Float>(20)
        gravYList = ArrayList<Float>(20)
        gravZList = ArrayList<Float>(20)

        laccelXList = ArrayList<Float>(20)
        laccelYList = ArrayList<Float>(20)
        laccelZList = ArrayList<Float>(20)

        rotXList = ArrayList<Float>(20)
        rotYList = ArrayList<Float>(20)
        rotZList = ArrayList<Float>(20)
        rotWList = ArrayList<Float>(20)

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var countdown: Int = 10

        // log/record sensor inputs

        binding.buttonTremor.setOnTouchListener(object : OnTouchListener {

            private var mHandler: Handler? = null

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

//                        Toast.makeText(view.context, "Keep pressing down for 10 seconds!", Toast.LENGTH_LONG).show()

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

                            countdown = 10 // reset countdown

                            println("gravXList: ${gravXList.toString()}")
                            println("gravYList: ${gravYList.toString()}")
                            println("gravZList: ${gravZList.toString()}")
//
//                            println("=====================================================")
//
//                            println("laccelXList: ${laccelXList.toString()}")
//                            println("laccelYList: ${laccelYList.toString()}")
//                            println("laccelZList: ${laccelZList.toString()}")
//
//                            println("=====================================================")
//
//                            println("rotXList: ${rotXList.toString()}")
//                            println("rotYList: ${rotYList.toString()}")
//                            println("rotZList: ${rotZList.toString()}")
//                            println("rotWList: ${rotWList.toString()}")

                            // average the sensor values
                            gravXAvg = takeAvg(gravXList)
                            gravYAvg = takeAvg(gravYList)
                            gravZAvg = takeAvg(gravZList)

                            laccelXAvg = takeAvg(laccelXList)
                            laccelYAvg = takeAvg(laccelYList)
                            laccelZAvg = takeAvg(laccelZList)

                            rotXAvg = takeAvg(rotXList)
                            rotYAvg = takeAvg(rotYList)
                            rotZAvg = takeAvg(rotZList)
                            rotWAvg = takeAvg(rotWList)

                            println("gravXAvg: $gravXAvg")
                            println("gravYAvg: $gravYAvg")
                            println("gravZAvg: $gravZAvg")

                            println("=====================================================")

                            println("laccelXAvg: $laccelXAvg")
                            println("laccelYAvg: $laccelYAvg")
                            println("laccelZAvg: $laccelZAvg")

                            println("=====================================================")

                            println("rotXAvg: $rotXAvg")
                            println("rotYAvg: $rotYAvg")
                            println("rotZAvg: $rotZAvg")
                            println("rotWAvg: $rotWAvg")

                            // clear the arraylists (to conserve memory & start fresh for next button press)
                            gravXList?.clear()
                            gravYList?.clear()
                            gravZList?.clear()

                            laccelXList?.clear()
                            laccelYList?.clear()
                            laccelZList?.clear()

                            rotXList?.clear()
                            rotYList?.clear()
                            rotZList?.clear()
                            rotWList?.clear()

                            // call method(s) to evaluate resting tremor given averaged sensor readings
                        }
                    }
                }
                return false
            }

            var mAction: Runnable = object : Runnable {
                override fun run() {

                    val mToast: Toast = Toast.makeText(view.context, "Keep pressing down for: $countdown seconds!", Toast.LENGTH_SHORT)

                    if (countdown > 0) {
                        mToast.setText("Keep pressing down for: ${countdown--} seconds!")
                        mToast.show()
                    } else {
                        Toast.makeText(view.context, "              STOP pressing down!              ", Toast.LENGTH_LONG).show()
                    }

                    gravXList?.add(gravX)
                    gravYList?.add(gravY)
                    gravZList?.add(gravZ)

                    laccelXList?.add(laccelX)
                    laccelYList?.add(laccelY)
                    laccelZList?.add(laccelZ)

                    rotXList?.add(rotvecX)
                    rotYList?.add(rotvecY)
                    rotZList?.add(rotvecZ)
                    rotWList?.add(rotvecW)

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

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            gravX = event.values[0]
            gravY = event.values[1]
            gravZ = event.values[2]
        } else if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            laccelX = event.values[0]
            laccelY = event.values[1]
            laccelZ = event.values[2]
        } else if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            rotvecX = event.values[0]
            rotvecY = event.values[1]
            rotvecZ = event.values[2]
            rotvecW = event.values[3]
        } else {
            println("WARN: Attempting to listen to sensor of unknown type!")
        }
    }

    override fun onResume() {
        super.onResume()
        mGravity?.also { gravity ->
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL)
        }

        mLinaccel?.also { laccelerometer ->
            sensorManager.registerListener(this, laccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        mRotvec?.also { rotvector ->
            sensorManager.registerListener(this, rotvector, SensorManager.SENSOR_DELAY_NORMAL)
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