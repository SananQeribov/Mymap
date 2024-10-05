package com.legalist.mymap

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.legalist.mymap.databinding.ActivityDetailBinding
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mmap: GoogleMap
    var placeName: String? =null
    lateinit var latitudeString: String
    lateinit var longitudeString: String
    var latitude: Double? = null
    var longitude: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        placeName = intent.getStringArrayListExtra("name").toString()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(p0: GoogleMap) {
       mmap = p0
        getData()

    }
    fun getData(){
        val query = ParseQuery.getQuery<ParseObject>("PlacesClass")
        query.whereEqualTo("name", placeName)
        query.findInBackground{ objects, e ->
            if (e !== null) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()

            } else {
                if (objects.size > 0) {
                    for (obj in objects) {
                        val parsfile = obj.getString("selectedimage") as ParseFile
                        parsfile.getDataInBackground { data, e ->
                            if (e == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                binding.imageviewDetailActivity.setImageBitmap(bitmap)
                                binding.nameTextDetailActivity.text = placeName
                                binding.typeTextDetailActivity.text = obj.getString("area")
                                binding.atmosphereTextDetailActivity.text = obj.getString("service")
                                latitudeString = obj.getString("latitude")
                                longitudeString = obj.getString("longitude")
                                latitude = latitudeString.toDoubleOrNull()
                                longitude = longitudeString.toDoubleOrNull()
                               mmap.clear()
                                val placeLocation = latitude?.let {
                                    longitude?.let { it1 ->
                                        LatLng(
                                            it, it1
                                        )
                                    }
                                }
                                placeLocation?.let { MarkerOptions().position(it).title(placeName) }
                                    ?.let {
                                        mmap.addMarker(
                                            it
                                        )
                                    }
                                placeLocation?.let {
                                    CameraUpdateFactory.newLatLngZoom(
                                        it,
                                        15F
                                    )
                                }?.let {
                                    mmap.moveCamera(
                                        it
                                    )
                                };


                            }

                        }


                    }

                }

            }

        }
    }
}