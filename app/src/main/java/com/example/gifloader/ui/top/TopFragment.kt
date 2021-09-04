package com.example.gifloader.ui.top

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
import com.example.gifloader.databinding.FragmentTopBinding
import com.example.gifloader.network.Connection
import com.example.gifloader.network.Retrofit
import com.example.gifloader.network.RetrofitApi
import com.example.gifloader.data.GifInfo
import com.example.gifloader.network.GlideLoader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopFragment : Fragment() {

    private lateinit var topViewModel: TopViewModel
    private var _binding: FragmentTopBinding? = null
    private val gifTopArray = ArrayList<GifInfo>()
    lateinit var ivTop: ImageView
    lateinit var tvTop: TextView
    var currentCountTop = 0
    var pageTop = 0
    var isErrorTop = false
    private val url = "https://developerslife.ru/"
    private val retrofitApi = Retrofit.getRetrofitClient(url)?.create(RetrofitApi::class.java)

    private val bindingTop get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        topViewModel =
            ViewModelProvider(this).get(TopViewModel::class.java)

        _binding = FragmentTopBinding.inflate(inflater, container, false)

        ivTop = bindingTop.ivTop
        tvTop = bindingTop.tvTop
        bindingTop.btnNextTop.setOnClickListener {
            nextTop()
        }
        bindingTop.btnBackTop.setOnClickListener { backTop() }
        topViewModel.signatureTop.observe(viewLifecycleOwner, {
            tvTop.text = it
        })
        bindingTop.btnReloadTop.setOnClickListener { tryToLoadTop() }

        topViewModel.imageUrlTop.observe(viewLifecycleOwner, {
            GlideLoader().loadGif(
                this@TopFragment,
                topViewModel.imageUrlTop.value,
                ivTop
            )
        })

        topViewModel.currentCountTop.observe(viewLifecycleOwner, {
            if (it == 0) {
                bindingTop.btnBackTop.visibility = View.INVISIBLE
            } else {
                bindingTop.btnBackTop.visibility = View.VISIBLE
            }
        })

        if (gifTopArray.size > 0) {
            setDataTop()
        } else {
            tryToLoadTop()
        }
        return bindingTop.root
    }

    override fun onResume() {
        super.onResume()
        currentCountTop = topViewModel.getCurrentCountTop()
        pageTop = topViewModel.pageTop.value!!
        gifTopArray.clear()
        for (i in topViewModel.getArrayTop()!!) {
            gifTopArray.add(i)
        }
        if (currentCountTop != 0) {
            setDataTop()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryToLoadTop() {
        if (context?.let { Connection().checkConnection(it) } == true) {
            loadDataTop(pageTop)
        } else {
            isErrorTop = true
            bindingTop.ivTop.setImageResource(com.example.gifloader.R.drawable.no_connection)
            bindingTop.tvTop.text = "Нет интернета!"
            bindingTop.btnReloadTop.visibility = View.VISIBLE
        }
    }

    private fun loadDataTop(page: Int) {
        retrofitApi?.getTop(page)?.enqueue(object : Callback<GifArray> {
            override fun onResponse(call: Call<GifArray>, response: Response<GifArray>) {
                if (response.isSuccessful) {
                    if (response.body()?.getSize() == 0) {
                        bindingTop.ivTop.setImageResource(com.example.gifloader.R.drawable.not_found)
                        bindingTop.tvTop.text = "Ничего не найдено!"
                        bindingTop.btnReloadTop.visibility = View.VISIBLE
                        isErrorTop = true
                    } else {
                        isErrorTop = false
                        bindingTop.btnReloadTop.visibility = View.INVISIBLE
                        for (i in 0..4) {
                            response.body()?.getItem(i)?.let { gifTopArray.add(it) }
                            response.body()?.getItem(i)?.let {
                                topViewModel.addItemTop(it)
                            }
                        }
                        bindingTop.btnNextTop.visibility = View.VISIBLE
                        setDataTop()

                    }
                }
            }

            override fun onFailure(call: Call<GifArray>, t: Throwable) {
            }
        })
    }

    private fun setDataTop() {
        bindingTop.btnNextTop.visibility = View.VISIBLE
        topViewModel.signatureTop.value =
            topViewModel.getArrayTop()?.get(currentCountTop)?.description
        topViewModel.imageUrlTop.value =
            topViewModel.getArrayTop()?.get(currentCountTop)?.gifURL
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun nextTop() {
        if (isErrorTop) {
            tryToLoadTop()
        } else {
            currentCountTop++
            topViewModel.currentCountTop.value = currentCountTop
            when (currentCountTop) {
                topViewModel.getArrayTop()?.size -> {
                    pageTop++
                    topViewModel.pageTop.value = pageTop
                    tryToLoadTop()
                }
                else -> {
                    setDataTop()
                }
            }
        }
    }

    private fun backTop() {
        currentCountTop--
        topViewModel.currentCountTop.value = currentCountTop
        setDataTop()
    }
}