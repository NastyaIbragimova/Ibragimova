package com.example.gifloader.ui.random

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gifloader.data.GifInfo

class RandomViewModel : ViewModel() {
    val signatureRandom = MutableLiveData<String>()
    val imageUrlRandom = MutableLiveData<String>()
    val currentCountRandom = MutableLiveData<Int>()
    val pageRandom = MutableLiveData<Int>()
    private val savedArrayRandom: MutableLiveData<ArrayList<GifInfo>> = MutableLiveData()

    init {
        savedArrayRandom.value = ArrayList()
        currentCountRandom.value = 0
        pageRandom.value = 0
    }

    fun getCurrentCountRandom(): Int {
        return currentCountRandom.value!!
    }

    fun addItemRandom(gifInfo: GifInfo) {
        if (savedArrayRandom.value?.contains(gifInfo) == false) {
            savedArrayRandom.value?.add(gifInfo)
        }
    }

    fun getArrayRandom(): ArrayList<GifInfo>? {
        return savedArrayRandom.value
    }
}