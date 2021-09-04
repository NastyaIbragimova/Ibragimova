package com.example.gifloader.ui.latest

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
import com.example.gifloader.data.GifArray
import com.example.gifloader.databinding.FragmentLatestBinding
import com.example.gifloader.network.Connection
import com.example.gifloader.network.Retrofit
import com.example.gifloader.network.RetrofitApi
import com.example.gifloader.data.GifInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.gifloader.network.GlideLoader



class LatestFragment : Fragment() {

    private lateinit var latestViewModel: LatestViewModel
    private var _binding: FragmentLatestBinding? = null
    private var gifLatestArray = ArrayList<GifInfo>()
    lateinit var ivlatest: ImageView
    lateinit var tvlatest: TextView
    var currentCountLatest = 0
    var pageHot = 0
    var isErrorLatest = false
    private val url = "https://developerslife.ru/"
    private val retrofitApi = Retrofit.getRetrofitClient(url)?.create(RetrofitApi::class.java)

    private val bindingLatest get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        latestViewModel =
            ViewModelProvider(this).get(LatestViewModel::class.java)

        _binding = FragmentLatestBinding.inflate(inflater, container, false)

        ivlatest = bindingLatest.ivLatest
        tvlatest = bindingLatest.tvLatest
        bindingLatest.btnNextLatest.setOnClickListener {
            nextLatest()
        }
        bindingLatest.btnBackLatest.setOnClickListener { backLatest() }
        latestViewModel.signatureLatest.observe(viewLifecycleOwner, {
            tvlatest.text = it
        })
        bindingLatest.btnReloadLatest.setOnClickListener { tryToLoadLatest() }

        latestViewModel.imageUrlLatest.observe(viewLifecycleOwner, {
            GlideLoader().loadGif(
                this@LatestFragment,
                latestViewModel.imageUrlLatest.value,
                ivlatest
            )
        })

        latestViewModel.currentCountLatest.observe(viewLifecycleOwner, {
            if (it == 0) {
                bindingLatest.btnBackLatest.visibility = View.INVISIBLE
            } else {
                bindingLatest.btnBackLatest.visibility = View.VISIBLE
            }
        })

        if (gifLatestArray.size > 0) {
            setDataLatest()
        } else {
            tryToLoadLatest()
        }
        currentCountLatest = latestViewModel.getCurrentCountLatest()
        return bindingLatest.root
    }

    override fun onResume() {
        super.onResume()
        currentCountLatest = latestViewModel.getCurrentCountLatest()
        pageHot = latestViewModel.pageLatest.value!!
        gifLatestArray.clear()
        for (i in latestViewModel.getArrayLatest()!!) {
            gifLatestArray.add(i)
        }
        if(currentCountLatest!=0){
            setDataLatest()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryToLoadLatest() {
        if (context?.let { Connection().checkConnection(it) } == true) {
            loadDataLatest(pageHot)
        } else {
            isErrorLatest = true
            bindingLatest.ivLatest.setImageResource(com.example.gifloader.R.drawable.no_connection)
            bindingLatest.tvLatest.text = "Нет интернета!"
            bindingLatest.btnReloadLatest.visibility = View.VISIBLE
        }
    }

    private fun loadDataLatest(page: Int) {
        retrofitApi?.getLatest(page)?.enqueue(object : Callback<GifArray> {
            override fun onResponse(call: Call<GifArray>, response: Response<GifArray>) {
                if (response.isSuccessful) {
                    if (response.body()?.getSize() == 0) {
                        bindingLatest.ivLatest.setImageResource(com.example.gifloader.R.drawable.not_found)
                        bindingLatest.tvLatest.text = "Ничего не найдено!"
                        bindingLatest.btnReloadLatest.visibility = View.VISIBLE
                        isErrorLatest = true
                    } else {
                        isErrorLatest = false
                        bindingLatest.btnReloadLatest.visibility = View.INVISIBLE
                        for (i in 0..4) {
                            response.body()?.getItem(i)?.let { gifLatestArray.add(it) }
                            response.body()?.getItem(i)?.let {
                                latestViewModel.addItemLatest(it)
                            }
                        }
                        bindingLatest.btnNextLatest.visibility = View.VISIBLE
                        setDataLatest()
                    }
                }
            }

            override fun onFailure(call: Call<GifArray>, t: Throwable) {
            }
        })
    }

    private fun setDataLatest() {
        bindingLatest.btnNextLatest.visibility = View.VISIBLE
        latestViewModel.signatureLatest.value =
            latestViewModel.getArrayLatest()?.get(currentCountLatest)?.description
        latestViewModel.imageUrlLatest.value =
            latestViewModel.getArrayLatest()?.get(currentCountLatest)?.gifURL
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun nextLatest() {
        if (isErrorLatest) {
            tryToLoadLatest()
        } else {
            currentCountLatest++
            latestViewModel.currentCountLatest.value = currentCountLatest
            when (currentCountLatest) {
                latestViewModel.getArrayLatest()?.size -> {
                    pageHot++
                    latestViewModel.pageLatest.value = pageHot
                    tryToLoadLatest()
                }
                else -> {
                    setDataLatest()
                }
            }
        }
    }

    private fun backLatest() {
        currentCountLatest--
        latestViewModel.currentCountLatest.value = currentCountLatest
        setDataLatest()
    }
}