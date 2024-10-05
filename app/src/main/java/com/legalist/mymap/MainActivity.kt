package com.legalist.mymap

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.legalist.mymap.databinding.ActivityMain2Binding
import com.parse.LogInCallback
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_name_signup_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        signUp()
        logIn()

    }

    fun signUp() {
        binding.button.setOnClickListener {
            val user = ParseUser()
            user.setUsername(binding.editTextText.text.toString())
            user.setPassword(binding.passwordSignupActivity.text.toString())

            user.signUpInBackground { e ->
                if (e != null) {
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG)
                        .show()

                } else {
                    Toast.makeText(applicationContext, "User signed in", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(applicationContext, LocationActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "Welcome: Sanan", Toast.LENGTH_LONG)
                        .show()

                }
            }

        }


    }

    fun logIn() {
        binding.button2.setOnClickListener {
            ParseUser.logInInBackground(
                binding.editTextText.text.toString(),
                binding.passwordSignupActivity.text.toString()
            ) { user, e ->
                if (e != null) {
                    Toast.makeText(
                        applicationContext,
                        e.localizedMessage,
                        Toast.LENGTH_LONG
                    )
                        .show()

                } else {
                    Toast.makeText(applicationContext, "User signed up", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(applicationContext, LocationActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "Welcome: Sanan", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
