package com.legalist.mymap

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.legalist.mymap.databinding.ActivityLocationBinding
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    lateinit var placeName: ArrayList<String>
    lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar-u ActionBar kimi təyin edin
        setSupportActionBar(binding.toolbar)
        placeName = ArrayList()
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, placeName)
        binding.listview.adapter = arrayAdapter
        dowland()
        binding.listview.setOnItemClickListener { _, _, position, _ ->

            val intent = Intent(this, DetailActivity::class.java).apply {
                putStringArrayListExtra("name", placeName)
            }
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_place, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addplace -> {
                // Menyu elementinə klik edildikdə nə baş verəcək
                val intent = Intent(this, CreatePlaceActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun dowland() {
        val query = ParseQuery.getQuery<ParseObject>("PlacesClass")
        query.findInBackground { objects, e ->
            if (e == null) {
              if (objects.size>0){
             placeName.clear()
                  for (obj in objects){
                  placeName.add(obj.getString("name"))
                   arrayAdapter.notifyDataSetChanged()

                  }

              }
            }
            else{

                Toast.makeText(this,e.localizedMessage,Toast.LENGTH_LONG).show();
            }


        }

    }
}

