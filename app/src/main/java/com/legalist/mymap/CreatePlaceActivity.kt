package com.legalist.mymap

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.legalist.mymap.databinding.ActivityCreatePlaceBinding

class CreatePlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePlaceBinding
    private var selectedImageUri: Uri? = null
    // Define a requestPermissionLauncher using the RequestPermission contract
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, launch photo picker
                launchNewPhotoPicker()
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                // Permission denied, show a toast message
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }

    // Define a newPhotoPicker launcher to pick an image
    private val newPhotoPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the picked image URI
            uri?.let {
                binding.imageView.setImageURI(it)
                selectedImageUri = it
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreatePlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectImage()
        next()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun selectImage() {
        binding.imageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Request the READ_MEDIA_IMAGES permission for Android Tiramisu and newer
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)

            } else {
                // Request the READ_EXTERNAL_STORAGE permission for older versions
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE.toString())
            }
        }
    }

    private fun launchNewPhotoPicker() {
        newPhotoPicker.launch("image/*")
    }

    fun next() {
        binding.button3.setOnClickListener {
            val placeClass = PlacesClass.instance
            val placename = binding.editTextText2.text.toString()
            val area = binding.editTextText3.text.toString()
            val service = binding.editTextText4.text.toString()

            placeClass?.apply {
                this.washCar = placename
                this.area = area
                this.service = service
                // Convert URI to Bitmap if an image is selected
                selectedImageUri?.let { uri ->
                    val bitmap = getBitmapFromUri(uri)
                    this.image = bitmap
                }


            }

            val intent = Intent(applicationContext,MapsActivity::class.java)
            startActivity(intent)
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


