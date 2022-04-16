package com.example.parkinsonarplus.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _text = MutableLiveData<String>().apply {
        value = "Welcome to ParkinsonarPlus"
    }
    var text: LiveData<String> = _text

    private var _gyroX = MutableLiveData<Float>().apply {
        value = 0.0F
    }
    var gyroX: MutableLiveData<Float> = _gyroX

    private var _gyroY = MutableLiveData<Float>().apply {
        value = 0.0F
    }
    var gyroY: MutableLiveData<Float> = _gyroY

    private var _gyroZ = MutableLiveData<Float>().apply {
        value = 0.0F
    }
    var gyroZ: MutableLiveData<Float> = _gyroZ
}