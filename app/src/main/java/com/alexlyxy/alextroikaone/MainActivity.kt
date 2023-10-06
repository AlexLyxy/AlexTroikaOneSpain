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
import com.google.protobuf.Empty
import org.json.JSONObject

const val API_KEY = "8ea82f6e78674e4996e181556230908"

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
                // adapter.submitList(parseDogs())
            }
        }
    }

    private fun init() = with(binding) {
        adapter = DoCoWeAdapter()
        //rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }

    //    SITE DOGS

    private fun getDogMessage() {
        val url = "https://dog.ceo/api/breed/hound/images"
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET,
            url,
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

    private fun parseDogsData(message: String): List<DoCoWeModel> {
        val mainObjectDog = JSONObject(message)
        return parseDogs(mainObjectDog)
    }

    private fun parseDogs(mainObjectDog: JSONObject): List<DoCoWeModel> {
        val dogArray = mainObjectDog.getJSONArray("message")
        val listDog = ArrayList<DoCoWeModel>()
        //for (i in 0 until dogArray.length()) {
        //for (i in 0 until 3) {
        //val day = daysArray[i] as JSONObject
        val item = DoCoWeModel(
            dogArray.getString(0),
            dogArray.getString(1),
            dogArray.getString(2),
            coinName = "",
            coinFullName = "",
            coinImageUrl = "l",
            time = "",
            condition = "",
            currentTemp = "",
            imageURL = ""
        )
        listDog.add(item)
        Log.d("MyLog", "DogListLast: $item")
        return listDog
    }

//    SITE COIN

    private fun getCoin(listD: List<DoCoWeModel>) {
        val url = "https://min-api.cryptocompare.com/data/top/totalvolfull?limit=10&tsym=USD"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { coinInfo ->
                val listC = parseCoinData(coinInfo)
                getWeatherData(city = "London", listD, listC)
            },
            {
                Log.d("MyLog", "Volley error: $it")
            }
        )
        queue.add(stringRequest)
    }

    private fun parseCoinData(coinInfo: String): List<DoCoWeModel> {
        val mainObjectCoin = JSONObject(coinInfo)
        return parseCoin(mainObjectCoin)
    }

    private fun parseCoin(mainObjectCoin: JSONObject): List<DoCoWeModel> {
        val coinArray = mainObjectCoin.getJSONArray("Data")
        val listCoin = ArrayList<DoCoWeModel>()
        //for (i in 0 until coinArray.length()) {
        //for (i in 0 until 1) {
        val item = DoCoWeModel(
            dogFaceOne = "",
            dogFaceTwo = "",
            dogFaceThree = "",
            coinArray
                .getJSONObject(0)
                .getJSONObject("CoinInfo")
                .getString("Name"),
            coinArray
                .getJSONObject(0)
                .getJSONObject("CoinInfo")
                .getString("FullName"),
            coinArray
                .getJSONObject(0)
                .getJSONObject("CoinInfo")
                .getString("ImageUrl"),
            time = "",
            condition = "",
            currentTemp = "",
            imageURL = ""
        )
        listCoin.add(item)
        Log.d("MyLog", "CoinListLast: $item")
        return listCoin
    }


    //   SITE WEATHER

    private fun getWeatherData(city: String, listD: List<DoCoWeModel>, listC: List<DoCoWeModel>) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                val listW = parseWeatherData(result)
                getDoCoWeList(listD, listC, listW)
                Log.d("MyLog", "Wether: $result")
            },
            { error ->
                Log.d("MyLog", "Volley error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String): List<DoCoWeModel> {
        val mainObjectWe = JSONObject(result)
        Log.d("MyLog", "MainObjectWeather: $mainObjectWe")
        return parseDays(mainObjectWe)
    }

    private fun parseDays(mainObjectWe: JSONObject): List<DoCoWeModel> {
        val listWe = ArrayList<DoCoWeModel>()
        val daysArray = mainObjectWe.getJSONObject("forecast").getJSONArray("forecastday")
        //for (i in 0 until daysArray.length()) {
        //for (i in 0 until 2) {
        val day = daysArray[0] as JSONObject
        val item = DoCoWeModel(
            dogFaceOne = "",
            dogFaceTwo = "",
            dogFaceThree = "",
            coinName = "BitCoin",
            coinFullName = "",
            coinImageUrl = "",
            time = day.getString("date"),
            condition = day.getJSONObject("day").getJSONObject("condition").getString("text"),
            currentTemp = day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt()
                .toString(),
            imageURL = day.getJSONObject("day").getJSONObject("condition").getString("icon")
        )
        listWe.add(item)
        Log.d("MyLog", "WeatherListLast: $item")
        return listWe
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
    }
}

