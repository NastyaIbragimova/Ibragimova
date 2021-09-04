package com.example.gifloader.ui.hot

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gifloader.data.GifInfo

class HotViewModel : ViewModel() {

    val signatureHot = MutableLiveData<String>()
    val imageUrlHot = MutableLiveData<String>()
    val currentCountHot = MutableLiveData<Int>()
    val pageHot = MutableLiveData<Int>()
    private val savedArrayHot: MutableLiveData<ArrayList<GifInfo>> = MutableLiveData()

    init {
        savedArrayHot.value = ArrayList()
        currentCountHot.value = 0
        pageHot.value = 0
    }

    fun getCurrentCountHot(): Int {
        return currentCountHot.value!!
    }

    fun addItemHot(gifInfo: GifInfo) {
        if (savedArrayHot.value?.contains(gifInfo) == false) {
            savedArrayHot.value?.add(gifInfo)
        }
    }

    fun getArrayHot(): ArrayList<GifInfo>? {
        return savedArrayHot.value
    }

}