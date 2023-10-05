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
            getCoin(emptyList())
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

    //    SITE COIN

    private fun getCoin(listD: List<DoCoWeModel>) {
        //val url = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=USD,JPY,EUR"
        val url = "https://min-api.cryptocompare.com/data/top/totalvolfull?limit=10&tsym=USD"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(com.android.volley.Request.Method.GET,
            url,
            { coinInfo ->
                val listC = parseCoinData(coinInfo)
                getWeatherData(city = "London", listD, listC)
                val mainObjectCoin = JSONObject(coinInfo)
                val infoCoin = mainObjectCoin.getJSONArray("Data")
                Log.d(
                    "MyLog", "InfoCoinName: ${
                        infoCoin
                            .getJSONObject(0)
                            .getJSONObject("CoinInfo")
                            .getString("Name")
                    }"
                )

                Log.d(
                    "MyLog", "InfoCoinFullName: ${
                        infoCoin
                            .getJSONObject(0)
                            .getJSONObject("CoinInfo")
                            .getString("FullName")
                    }"
                )

                Log.d(
                    "MyLog", "InfoCoinImageOne: ${
                        infoCoin
                            .getJSONObject(0)
                            .getJSONObject("CoinInfo")
                            .getString("ImageUrl")
                    }"
                )
                Log.d(
                    "MyLog", "InfoCoinImageTwo: ${
                        infoCoin
                            .getJSONObject(0)
                            .getJSONObject("CoinInfo")
                            .getString("Url")
                    }"
                )
            },
            {
                Log.d("MyLog", "Volley error: $it")
            }
        )
        queue.add(stringRequest)
    }

    private fun parseCoinData(coinInfo: String): List<DoCoWeModel> {
        val mainObjectCoin = JSONObject(coinInfo)
        Log.d("MyLog", "MainObjectCoin: $mainObjectCoin")
        return parseCoin(mainObjectCoin)
    }

    private fun parseCoin(mainObjectCoin: JSONObject): List<DoCoWeModel> {
        val coinArray = mainObjectCoin.getJSONArray("Data")
        Log.d("MyLog", "CoinArray: $coinArray")
        val listCoin = ArrayList<DoCoWeModel>()

        //for (i in 0 until coinArray.length()) {
        for (i in 0 until 2) {
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
                    .getString("Url"),
                coinArray
                    .getJSONObject(0)
                    .getJSONObject("CoinInfo")
                    .getString("ImageUrl"),
                time = "26.09.2023",
                //day.getString("date"),
                condition = "Clear",
                //day.getJSONObject("day").getJSONObject("condition").getString("text"),
                currentTemp = "25",
                //day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                imageURL = "ImageUrl"
                //day.getJSONObject("day").getJSONObject("condition").getString("icon")
            )
            listCoin.add(item)
            Log.d("MyLog", "CoinListLast: $listCoin")
        }
        return listCoin
    }

    // Parse 3 lists

    private fun getDoCoWeList(listD: List<DoCoWeModel>, listC: List<DoCoWeModel>, listW: List<DoCoWeModel>){
        val mainList = ArrayList<DoCoWeModel>()
        for (i in 0..1) {
            mainList.add(
                DoCoWeModel(
                    dogFaceOne = "",
                    dogFaceTwo = "",
                    dogFaceThree = "",
                    //listD[i].dogFaceOne,
                    //listD[i].dogFaceTwo,
                    //listD[i].dogFaceThree,
                    listC[i].coinName,
                    listC[i].coinFullName,
                    listC[i].coinUrl,
                    listC[i].coinImageUrl,
                    listW[i].time,
                    listW[i].condition,
                    listW[i].currentTemp,
                    listW[i].imageURL
                )
            )
        }
        model.liveDataList.value = mainList
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

        for (i in 0 until daysArray.length()) {
            //for (i in 0 until 2) {
            val day = daysArray[i] as JSONObject
            val item = DoCoWeModel(
                dogFaceOne = "",
                dogFaceTwo = "",
                dogFaceThree = "",
                coinName = "BitCoin",
                coinFullName = "",
                coinImageUrl = "",
                coinUrl = "",
                time = day.getString("date"),
                condition = day.getJSONObject("day").getJSONObject("condition").getString("text"),
                currentTemp = day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt()
                    .toString(),
                imageURL = day.getJSONObject("day").getJSONObject("condition").getString("icon")
            )
            listWe.add(item)

            val time = day.getString("date")
            val condition = day.getJSONObject("day").getJSONObject("condition").getString("text")
            val currentTemp =
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString()
            val imageURL = day.getJSONObject("day").getJSONObject("condition").getString("icon")

            Log.d("MyLog", "Date: $time ")
            Log.d("MyLog", "Condition: $condition")
            Log.d("MyLog", "CurrentTemp: $currentTemp")
            Log.d("MyLog", "ImageURL: $imageURL")

            Log.d("MyLog", "WeatherListLast: $listWe")
        }
        return listWe
    }

    //    SITE DOGS

    private fun getDogMessage() {
        //val url = "https://dog.ceo/api/breeds/image/random"
        //val url = "https://dog.ceo/api/breed/hound/afghan/images"
        val url = "https://dog.ceo/api/breed/hound/images"
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET,
            url,
            { message ->
                val listD = parseDogsData(message)
                getCoin(listD)
                Log.d("MyLog", "MessDog: $message")
                val mainObjectDog = JSONObject(message)
                val dogArray = mainObjectDog.getJSONArray("message")

                val dogsFaceOne = dogArray.getString(0)
                val dogsFaceTwo = dogArray.getString(1)
                val dogsFaceThree = dogArray.getString(2)

                Log.d("MyLog", "MessDogArray: $dogArray")

                Log.d("MyLog", "MessDogArrayOne: $dogsFaceOne")
                Log.d("MyLog", "MessDogArrayTwo: $dogsFaceTwo")
                Log.d("MyLog", "MessDogArrayThree: $dogsFaceThree")

            },
            { error ->
                Log.d("MyLog", "Volley dog error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseDogsData(message: String): List<DoCoWeModel> {
        val mainObjectDog = JSONObject(message)
        Log.d("MyLog", "MainObjectDog: $mainObjectDog")
        return parseDogs(mainObjectDog)
    }

    private fun parseDogs(mainObjectDog: JSONObject): List<DoCoWeModel> {
        val dogArray = mainObjectDog.getJSONArray("message")
        Log.d("MyLog", "DogArray: $dogArray")
        val listDog = ArrayList<DoCoWeModel>()
        // val daysArray = mainObjectWe.getJSONObject("forecast").getJSONArray("forecastday")

        //for (i in 0 until dogArray.length()) {
        for (i in 0 until 3) {
            //val day = daysArray[i] as JSONObject
            val item = DoCoWeModel(
                dogArray.getString(0),
                dogArray.getString(1),
                dogArray.getString(2),
                coinName = "London",
                coinFullName = "FullName",
                coinImageUrl = "CoinImageUrl",
                coinUrl = "CoinUrl",
                time = "26.09.2023",
                //day.getString("date"),
                condition = "Clear",
                //day.getJSONObject("day").getJSONObject("condition").getString("text"),
                currentTemp = "25",
                //day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                imageURL = "ImageUrl"
                //day.getJSONObject("day").getJSONObject("condition").getString("icon")
            )

            listDog.add(item)
            Log.d("MyLog", "DogListLast: $listDog")
        }
        return listDog
    }
}

