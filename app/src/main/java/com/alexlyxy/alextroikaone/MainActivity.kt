package com.alexlyxy.alextroikaone

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexlyxy.alextroikaone.adapters.DoCoWeAdapter
import com.alexlyxy.alextroikaone.adapters.DoCoWeModel
import com.alexlyxy.alextroikaone.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: DoCoWeAdapter

    private lateinit var binding: ActivityMainBinding
    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bGet.setOnClickListener {
            //getCoin(emptyList())
            getDogMessage()
            init()
            model.liveDataList.observe(this) {
                adapter.submitList(it)
                Log.d(
                    "MyLog", "Size: ${it.size}"
                )
            }
        }
    }

    private fun init() = with(binding) {
        adapter = DoCoWeAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }

    //    SITE DOGS

    private fun getDogMessage() {
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET,
            urlDog,
            { message ->
                val listD = parseDogsData(message)
                getCoin(listD)
            },
            { error ->
                Log.d("MyLog", "Volley dog error: $error")
            }
        )
        queue.add(request)
    }

//    SITE COIN

    private fun getCoin(listD: List<DoCoWeModel>) {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET,
            urlCoin,
            { coinInfo ->
                val listC = parseCoinData(coinInfo)
                getWeatherData(listD, listC)
            },
            {
                Log.d("MyLog", "Volley error: $it")
            }
        )
        queue.add(stringRequest)
    }

    //   SITE WEATHER

    private fun getWeatherData( listD: List<DoCoWeModel>, listC: List<DoCoWeModel>) {
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET,
            urlWeather,
            { result ->
                val listW = parseWeatherData(result)
                getDoCoWeList(listD, listC, listW)
            },
            { error ->
                Log.d("MyLog", "Volley error: $error")
            }
        )
        queue.add(request)
    }

    // Parse 3 lists

    private fun getDoCoWeList(
        listD: List<DoCoWeModel>,
        listC: List<DoCoWeModel>,
        listW: List<DoCoWeModel>
    ) {
        val mainList = ArrayList<DoCoWeModel>()
        for (i in 0..3) {
            mainList.add(
                DoCoWeModel(
                    listD[0].dogFaceOne,
                    listD[0].dogFaceTwo,
                    listD[0].dogFaceThree,
                    listC[0].coinName,
                    listC[0].coinFullName,
                    listC[0].coinImageUrl,
                    listW[0].time,
                    listW[0].condition,
                    listW[0].currentTemp,
                    listW[0].imageURL
                )
            )
        }
        model.liveDataList.value = mainList
        Log.d("MyLog", "MainList : $mainList")
    }
}



