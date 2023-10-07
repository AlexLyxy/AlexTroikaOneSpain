package com.alexlyxy.alextroikaone

import android.util.Log
import com.alexlyxy.alextroikaone.adapters.DoCoWeModel
import org.json.JSONObject

const val API_KEY = "8ea82f6e78674e4996e181556230908"

const val urlDog = "https://dog.ceo/api/breed/hound/images"

fun parseDogs(mainObjectDog: JSONObject): List<DoCoWeModel> {
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

fun parseDogsData(message: String): List<DoCoWeModel> {
    val mainObjectDog = JSONObject(message)
    return parseDogs(mainObjectDog)
}

const val urlCoin = "https://min-api.cryptocompare.com/data/top/totalvolfull?limit=10&tsym=USD"

fun parseCoin(mainObjectCoin: JSONObject): List<DoCoWeModel> {
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

fun parseCoinData(coinInfo: String): List<DoCoWeModel> {
    val mainObjectCoin = JSONObject(coinInfo)
    return parseCoin(mainObjectCoin)
}

const val urlWeather = "https://api.weatherapi.com/v1/forecast.json?key=" +
        API_KEY +
        "&q=" +
        "city" +
        "&days=" +
        "3" +
        "&aqi=no&alerts=no"

fun parseDays(mainObjectWe: JSONObject): List<DoCoWeModel> {
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
fun parseWeatherData(result: String): List<DoCoWeModel> {
    val mainObjectWe = JSONObject(result)
    return parseDays(mainObjectWe)
}
