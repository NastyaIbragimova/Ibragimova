package com.example.gifloader.network

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gifloader.ui.latest.LatestFragment

class GlideLoader {
    private val requestOptions = RequestOptions().placeholder(com.example.gifloader.R.drawable.load)

    fun loadGif(context: Fragment, url: String?, imageView: ImageView){
            Glide.with(context).load(url)
                .onlyRetrieveFromCache(true)
                .apply(requestOptions)
                .error(Glide.with(context).load(url))
                .into(imageView)
        }
}