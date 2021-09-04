package com.example.gifloader.ui.random

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gifloader.R
import com.example.gifloader.databinding.FragmentRandomBinding
import com.example.gifloader.network.Connection
import com.example.gifloader.network.Retrofit
import com.example.gifloader.network.RetrofitApi
import com.example.gifloader.data.GifInfo
import com.example.gifloader.network.GlideLoader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RandomFragment : Fragment() {

    private lateinit var randomViewModel: RandomViewModel
    private var _binding: FragmentRandomBinding? = null
    private val gifRandomArray = ArrayList<GifInfo>()
    lateinit var ivRandom: ImageView
    lateinit var tvRandom: TextView
    var currentCountRandom = 0
    var isErrorRandom = false
    private val url = "https://developerslife.ru/"
    private val retrofitApi = Retrofit.getRetrofitClient(url)?.create(RetrofitApi::class.java)

    private val bindingRandom get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        randomViewModel = ViewModelProvider(this).get(RandomViewModel::class.java)

        _binding = FragmentRandomBinding.inflate(inflater, container, false)

        ivRandom = bindingRandom.ivRandom
        tvRandom = bindingRandom.tvRandom
        bindingRandom.btnNextRandom.setOnClickListener {
            nextRandom()
        }
        bindingRandom.btnBackRandom.setOnClickListener { backRandom() }
        randomViewModel.signatureRandom.observe(viewLifecycleOwner, {
            tvRandom.text = it
        })
        bindingRandom.btnReloadRandom.setOnClickListener { tryToLoadRandom() }



        randomViewModel.imageUrlRandom.observe(viewLifecycleOwner, {
            GlideLoader().loadGif(
                this@RandomFragment,
                randomViewModel.imageUrlRandom.value,
                ivRandom
            )
        })

        randomViewModel.currentCountRandom.observe(viewLifecycleOwner, {
            if (it == 0) {
                bindingRandom.btnBackRandom.visibility = View.INVISIBLE
            } else {
                bindingRandom.btnBackRandom.visibility = View.VISIBLE
            }
        })

        if (gifRandomArray.size > 0) {
            setDataRandom()
        } else {
            tryToLoadRandom()
        }
        return bindingRandom.root
    }

    override fun onResume() {
        super.onResume()
        currentCountRandom = randomViewModel.getCurrentCountRandom()
        gifRandomArray.clear()
        for (i in randomViewModel.getArrayRandom()!!) {
            gifRandomArray.add(i)
        }
        if(currentCountRandom!=0){
            setDataRandom()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryToLoadRandom() {
        if (context?.let { Connection().checkConnection(it) } == true) {
            loadDataRandom()
        } else {
            isErrorRandom = true
            bindingRandom.ivRandom.setImageResource(R.drawable.no_connection)
            bindingRandom.tvRandom.text = "Нет интернета!"
            bindingRandom.btnReloadRandom.visibility = View.VISIBLE
        }
    }

    private fun loadDataRandom() {
        retrofitApi?.getRandom()?.enqueue(object : Callback<GifInfo> {
            override fun onResponse(call: Call<GifInfo>, response: Response<GifInfo>) {
                if (response.isSuccessful) {
                    if (response.body()?.id == 0) {
                        bindingRandom.ivRandom.setImageResource(R.drawable.not_found)
                        bindingRandom.tvRandom.text = "Ничего не найдено!"
                        bindingRandom.btnReloadRandom.visibility = View.VISIBLE
                        isErrorRandom = true
                    } else {
                        isErrorRandom = false
                        bindingRandom.btnReloadRandom.visibility = View.INVISIBLE
                        response.body()?.let { gifRandomArray.add(it) }
                        response.body()?.let { randomViewModel.addItemRandom(it) }
                        bindingRandom.btnNextRandom.visibility = View.VISIBLE
                        setDataRandom()

                    }
                }
            }

            override fun onFailure(call: Call<GifInfo>, t: Throwable) {

            }
        })
    }

    private fun setDataRandom() {
        bindingRandom.btnNextRandom.visibility = View.VISIBLE
        randomViewModel.signatureRandom.value =
            randomViewModel.getArrayRandom()?.get(currentCountRandom)?.description
        randomViewModel.imageUrlRandom.value =
            randomViewModel.getArrayRandom()?.get(currentCountRandom)?.gifURL
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun nextRandom() {
        if (isErrorRandom) {
            tryToLoadRandom()
        } else {
            currentCountRandom++
            randomViewModel.currentCountRandom.value = currentCountRandom
            when (currentCountRandom) {
                randomViewModel.getArrayRandom()?.size -> {
                    tryToLoadRandom()
                }
                else -> {
                    setDataRandom()
                }
            }
        }
    }

    private fun backRandom() {
        currentCountRandom--
        randomViewModel.currentCountRandom.value = currentCountRandom
        setDataRandom()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}