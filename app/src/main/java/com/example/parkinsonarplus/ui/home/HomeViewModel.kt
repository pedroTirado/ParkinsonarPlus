package com.example.parkinsonarplus.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _text = MutableLiveData<String>().apply {
        value = "Welcome to ParkinsonarPlus"
    }
    var text: LiveData<String> = _text

    private var _gravZ = MutableLiveData<Float>().apply {
        value = 0.0F
    }
    var gravZ: MutableLiveData<Float> = _gravZ

    private var _laccelMagn = MutableLiveData<Float>().apply {
        value = 0.0F
    }
    var laccelMagn: MutableLiveData<Float> = _laccelMagn

    private var _atRest = MutableLiveData<Boolean>().apply {
        value = false
    }
    var atRest: MutableLiveData<Boolean> = _atRest
}