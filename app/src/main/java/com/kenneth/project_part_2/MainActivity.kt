package com.kenneth.project_part_2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val API_URL = "https://api.weatherapi.com/v1/current.json"


    private val API_KEY = "c5eef02d2cc44145bf3234851231412"

    //private val locationHelper = LocationHelper.instance




    private lateinit var temperatureTextView: TextView
    private lateinit var feelsLikeTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var uvTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var conditionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        temperatureTextView = findViewById(R.id.temperatureTextView)
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView)
        windTextView = findViewById(R.id.windTextView)
        humidityTextView = findViewById(R.id.humidityTextView)
        uvTextView = findViewById(R.id.uvTextView)
        visibilityTextView = findViewById(R.id.visibilityTextView)
        conditionTextView = findViewById(R.id.conditionTextView)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {

            getLocation()
        }
    }


    private fun getLocation() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, LocationListener {  })

        val locationListener = LocationListener {
            // your code to handle location update
        }

        // Get the last known location
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)


        if (location != null) {

            val latitude = location.latitude
            val longitude = location.longitude


            getWeather(latitude, longitude)
        } else {

            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getWeather(latitude: Double, longitude: Double) {

        val url = "$API_URL?key=$API_KEY&q=$latitude,$longitude"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->

                val current = response.getJSONObject("current")
                val temperature = current.getDouble("temp_c")
                val feelsLike = current.getDouble("feelslike_c")
                val windSpeed = current.getDouble("wind_kph")
                val windDirection = current.getString("wind_dir")
                val humidity = current.getDouble("humidity")
                val uv = current.getDouble("uv")
                val visibility = current.getDouble("vis_km")
                val condition = current.getJSONObject("condition").getString("text")


                temperatureTextView.text = "Temperature: $temperature°C"
                feelsLikeTextView.text = "Feels like: $feelsLike°C"
                windTextView.text = "Wind: $windSpeed km/h $windDirection"
                humidityTextView.text = "Humidity: $humidity%"
                uvTextView.text = "UV Index: $uv"
                visibilityTextView.text = "Visibility: $visibility km"
                conditionTextView.text = "Condition: $condition"
            },
            { error ->

                Toast.makeText(this, "Unable to get weather information", Toast.LENGTH_SHORT).show()
            })


        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}
