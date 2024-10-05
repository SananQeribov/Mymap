package com.legalist.mymap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.legalist.mymap.databinding.ActivityMapsBinding
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.ByteArrayOutputStream

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var sharedprefence: SharedPreferences
    lateinit var latitudeString: String
    lateinit var longitudeString: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_place, menu)

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.saveplace -> {
                upload()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("CommitPrefEdits")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        //mMap.clear() // Clear previous markers
        locationListener = LocationListener { location ->
            sharedprefence = this.getSharedPreferences("com.legalist.mymap", MODE_PRIVATE)
            val firstTimeCheck = sharedprefence.getBoolean("notfirstimecheck", false)
            if (!firstTimeCheck) {
                val userLocation = LatLng(location.latitude, location.longitude)
                //mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                sharedprefence.edit().putBoolean("notfirstimecheck", true)

            }

        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            // Start receiving location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
            // Get the last known location
            val lastKnowLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnowLocation != null) {
                val lastUserLocation = LatLng(lastKnowLocation.latitude, lastKnowLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
            } else {
                Toast.makeText(this, "Unable to get last known location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start fetching location updates
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                    val lastKnowLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastKnowLocation != null) {
                        val lastUserLocation =
                            LatLng(lastKnowLocation.latitude, lastKnowLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                    }
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        val latitude = p0.latitude
        val longtitude = p0.longitude
        latitudeString = latitude.toString()
        longitudeString = longtitude.toString()
        mMap.addMarker(MarkerOptions().title("New Place").position(p0))
        Toast.makeText(this, "Saved Location", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun upload() {
        val placeclass = PlacesClass.instance

        val avtoYuma = placeclass?.washCar
        val area = placeclass?.area
        val sevice = placeclass?.service
        val placeImage = placeclass?.image
        val placeObject = ParseObject("PlacesClass")
        //val drawable = resources.getDrawable(R.drawable.selectedimage, null)
       // val bitmap = (drawable as BitmapDrawable).bitmap

        val byteArrayOutputStream = ByteArrayOutputStream()
        if (placeImage != null) {
            placeImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
        }
        val bytes = byteArrayOutputStream.toByteArray()

        val parsFile = ParseFile("selectedimage.png", bytes)

        placeObject.apply {
            put("selectedimage", parsFile)
            put("name", avtoYuma)
            put("area", area)
            put("service", sevice)
            put("latitude", latitudeString)
            put("longitude", longitudeString)


        }
        placeObject.saveInBackground { e ->
            if (e != null) {
                Toast.makeText(
                    applicationContext,
                    e.localizedMessage ?: "Unknown error",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e("ParseError", "Failed to save place: ${e.localizedMessage}")
            } else {
                val intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
                Toast.makeText(applicationContext, "Saved this place", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    }







