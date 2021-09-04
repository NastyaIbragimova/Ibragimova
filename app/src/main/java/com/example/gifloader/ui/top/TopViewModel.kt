package com.example.gifloader.ui.top

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gifloader.data.GifInfo

class TopViewModel : ViewModel() {
    val signatureTop = MutableLiveData<String>()
    val imageUrlTop = MutableLiveData<String>()
    val currentCountTop = MutableLiveData<Int>()
    val pageTop = MutableLiveData<Int>()
    private val savedArrayTop: MutableLiveData<ArrayList<GifInfo>> = MutableLiveData()

    init {
        savedArrayTop.value = ArrayList()
        currentCountTop.value = 0
        pageTop.value = 0
    }

    fun getCurrentCountTop(): Int {
        return currentCountTop.value!!
    }

    fun addItemTop(gifInfo: GifInfo) {
        if (savedArrayTop.value?.contains(gifInfo) == false) {
            savedArrayTop.value?.add(gifInfo)
        }
    }

    fun getArrayTop(): ArrayList<GifInfo>? {
        return savedArrayTop.value
    }
}