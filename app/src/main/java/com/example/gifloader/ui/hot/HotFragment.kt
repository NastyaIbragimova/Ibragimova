package com.example.gifloader.ui.hot

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gifloader.data.GifArray
import com.example.gifloader.R
import com.example.gifloader.databinding.FragmentHotBinding
import com.example.gifloader.network.Connection
import com.example.gifloader.network.Retrofit
import com.example.gifloader.network.RetrofitApi
import com.example.gifloader.data.GifInfo
import com.example.gifloader.network.GlideLoader


class HotFragment : Fragment() {

    private lateinit var hotViewModel: HotViewModel
    private var hotBinding: FragmentHotBinding? = null
    private val gifHotArray = ArrayList<GifInfo>()
    lateinit var ivHot: ImageView
    lateinit var tvHot: TextView
    var currentCountHot = 0
    var pageHot = 0
    var isErrorHot = false
    private val url = "https://developerslife.ru/"
    private val retrofitApi = Retrofit.getRetrofitClient(url)?.create(RetrofitApi::class.java)


    private val bindingHot get() = hotBinding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hotViewModel = ViewModelProvider(this).get(HotViewModel::class.java)
        hotBinding = FragmentHotBinding.inflate(inflater, container, false)
        ivHot = bindingHot.ivHot
        tvHot = bindingHot.tvHot
        bindingHot.btnNextHot.setOnClickListener {
            nextHot()
        }
        bindingHot.btnBackHot.setOnClickListener { backHot() }
        hotViewModel.signatureHot.observe(viewLifecycleOwner, {
            tvHot.text = it
        })
        bindingHot.btnReloadHot.setOnClickListener { tryToLoadHot() }

        hotViewModel.imageUrlHot.observe(viewLifecycleOwner, {
            GlideLoader().loadGif(
                this@HotFragment,
                hotViewModel.imageUrlHot.value,
                ivHot
            )
        })

        hotViewModel.currentCountHot.observe(viewLifecycleOwner, {
            if (it == 0) {
                bindingHot.btnBackHot.visibility = View.INVISIBLE
            } else {
                bindingHot.btnBackHot.visibility = View.VISIBLE
            }
        })

        if (gifHotArray.size > 0) {
            setDataHot()
        } else {
            tryToLoadHot()
        }

        return bindingHot.root
    }

    override fun onResume() {
        super.onResume()
        currentCountHot = hotViewModel.getCurrentCountHot()
        pageHot = hotViewModel.pageHot.value!!
        gifHotArray.clear()
        for (i in hotViewModel.getArrayHot()!!) {
            gifHotArray.add(i)
        }
        if(currentCountHot!=0){
            setDataHot()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryToLoadHot() {
        if (context?.let { Connection().checkConnection(it) } == true) {
            loadDataHot(pageHot)
        } else {
            isErrorHot = true
            bindingHot.ivHot.setImageResource(R.drawable.no_connection)
            bindingHot.tvHot.text = "Нет интернета!"
            bindingHot.btnReloadHot.visibility = View.VISIBLE
        }
    }

    private fun loadDataHot(page: Int) {
        retrofitApi?.getHot(page)?.enqueue(object : Callback<GifArray> {
            override fun onResponse(call: Call<GifArray>, response: Response<GifArray>) {
                if (response.isSuccessful) {Log.e("MyLog", "response   ${response.raw()}")
                    if (response.body()?.getSize() == 0) {
                        bindingHot.ivHot.setImageResource(R.drawable.not_found)
                        bindingHot.tvHot.text = "Ничего не найдено!"
                        bindingHot.btnReloadHot.visibility = View.VISIBLE
                        isErrorHot = true
                    } else {
                        isErrorHot = false
                        bindingHot.btnReloadHot.visibility = View.INVISIBLE
                        for (i in 0..4) {
                            response.body()?.getItem(i)?.let { gifHotArray.add(it) }
                            response.body()?.getItem(i)?.let {
                                hotViewModel.addItemHot(it)
                            }
                        }
                        bindingHot.btnNextHot.visibility = View.VISIBLE
                        setDataHot()

                    }
                }
            }

            override fun onFailure(call: Call<GifArray>, t: Throwable) {
            }
        })
    }

    private fun setDataHot() {
        bindingHot.btnNextHot.visibility = View.VISIBLE
        hotViewModel.signatureHot.value =
            hotViewModel.getArrayHot()?.get(currentCountHot)?.description
        hotViewModel.imageUrlHot.value =
            hotViewModel.getArrayHot()?.get(currentCountHot)?.gifURL
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun nextHot() {
        if (isErrorHot) {
            tryToLoadHot()
        } else {
            currentCountHot++
            hotViewModel.currentCountHot.value = currentCountHot
            when (currentCountHot) {
                hotViewModel.getArrayHot()?.size -> {
                    pageHot++
                    hotViewModel.pageHot.value = pageHot
                    tryToLoadHot()
                }
                else -> {
                    setDataHot()
                }
            }
        }
    }

    private fun backHot() {
        currentCountHot--
        hotViewModel.currentCountHot.value = currentCountHot
        setDataHot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hotBinding = null
    }
}

