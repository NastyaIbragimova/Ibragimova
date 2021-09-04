package com.example.gifloader.ui.latest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gifloader.data.GifInfo

class LatestViewModel : ViewModel() {

    val signatureLatest = MutableLiveData<String>()
    val imageUrlLatest = MutableLiveData<String>()
    val currentCountLatest = MutableLiveData<Int>()
    val pageLatest = MutableLiveData<Int>()
    private val savedArrayLatest: MutableLiveData<ArrayList<GifInfo>> = MutableLiveData()

    init {
        savedArrayLatest.value = ArrayList()
        currentCountLatest.value = 0
        pageLatest.value = 0
    }

    fun getCurrentCountLatest(): Int {
        return currentCountLatest.value!!
    }

    fun addItemLatest(gifInfo: GifInfo) {
        if (savedArrayLatest.value?.contains(gifInfo) == false) {
            savedArrayLatest.value?.add(gifInfo)
        }
    }

    fun getArrayLatest(): ArrayList<GifInfo>? {
        return savedArrayLatest.value
    }
}