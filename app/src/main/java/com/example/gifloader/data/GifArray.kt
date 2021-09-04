package com.example.gifloader.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GifArray {

    @SerializedName("result")
    @Expose
    private val gifInfos:ArrayList<GifInfo> = ArrayList()

    fun getItem(int: Int): GifInfo {
        return gifInfos[int]
    }

    fun getSize() = gifInfos.size
}